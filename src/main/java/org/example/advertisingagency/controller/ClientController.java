package org.example.advertisingagency.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.dto.client.CreateClientInput;
import org.example.advertisingagency.dto.client.UpdateClientInput;
import org.example.advertisingagency.exception.EntityInUseException;
import org.example.advertisingagency.model.Client;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.ClientRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Controller
public class ClientController {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository, ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
    }

    // ====== QUERY ======

    @QueryMapping
    public List<Client> clients() {
        return clientRepository.findAll();
    }

    @QueryMapping
    public Client client(@Argument Integer id) {
        return clientRepository.findById(id).orElse(null);
    }

    // ====== MUTATION ======

    @MutationMapping
    @Transactional
    public Client createClient(@Argument CreateClientInput input) {
        Client client = new Client();
        client.setName(input.getName());
        client.setEmail(input.getEmail());
        client.setPhoneNumber(input.getPhoneNumber());
        return clientRepository.save(client);
    }

    @MutationMapping
    @Transactional
    public Client updateClient(@Argument Integer id, @Argument UpdateClientInput input) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));

        if (input.getName() != null) client.setName(input.getName());
        if (input.getEmail() != null) client.setEmail(input.getEmail());
        if (input.getPhoneNumber() != null) client.setPhoneNumber(input.getPhoneNumber());

        return clientRepository.save(client);
    }

    @MutationMapping
    @Transactional
    public boolean deleteClient(@Argument Integer id) {
        if (!clientRepository.existsById(id)) {
            return false;
        }
        try {
            clientRepository.deleteById(id);
            clientRepository.flush();
            return true;
        }catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("Клієнт має пов'язані записи у проектах");
        }
    }

    // ====== SCHEMA MAPPING ======

    @SchemaMapping(typeName = "Client", field = "projects")
    public List<Project> getProjects(Client client) {
        if (client.getId() == null) {
            return Collections.emptyList();
        }
        return projectRepository.findAllByClient_Id(client.getId());
    }
}
