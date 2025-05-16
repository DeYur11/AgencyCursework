package org.example.advertisingagency.util;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.example.advertisingagency.exception.EntityInUseException;
import org.example.advertisingagency.exception.InvalidMaterialState;
import org.example.advertisingagency.exception.InvalidStateTransitionException;
import org.example.advertisingagency.exception.RollbackException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GraphQLExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex,
                                                     DataFetchingEnvironment env) {

        List<GraphQLError> errors = switch (ex) {
            case InvalidStateTransitionException e -> List.of(buildError(e, env, ErrorType.BAD_REQUEST));
            case InvalidMaterialState          e -> List.of(buildError(e, env, ErrorType.FORBIDDEN));
            case EntityInUseException          e -> List.of(buildError(e, env, ErrorType.FORBIDDEN));
            case RollbackException ignored ->  List.of(buildError(ignored, env, ErrorType.BAD_REQUEST));
            default                            -> List.of();
        };

        return errors.isEmpty() ? Mono.empty() : Mono.just(errors);
    }

    private GraphQLError buildError(Throwable ex,
                                    DataFetchingEnvironment env,
                                    ErrorType type) {

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(type)
                .build();
    }
}
