package org.example.advertisingagency.util;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.example.advertisingagency.exception.InvalidMaterialState;
import org.example.advertisingagency.exception.InvalidStateTransitionException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class GraphQLExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        if (exception instanceof InvalidStateTransitionException) {
            return Mono.just(
                    Collections.singletonList(GraphqlErrorBuilder.newError(environment)
                            .message(exception.getMessage())
                            .errorType(ErrorType.BAD_REQUEST)
                            .build())
            );
        }else if (exception instanceof InvalidMaterialState){
            return Mono.just(
                    Collections.singletonList(GraphqlErrorBuilder.newError(environment)
                            .message(exception.getMessage())
                            .errorType(ErrorType.BAD_REQUEST)
                            .build())
            );
        }
        return Mono.empty(); // не обробляємо — передаємо далі
    }
}