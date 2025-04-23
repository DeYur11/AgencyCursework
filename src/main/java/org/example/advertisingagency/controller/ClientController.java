package org.example.advertisingagency.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.advertisingagency.model.Client;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.ClientRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

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

    @QueryMapping
    public List<Client> clients() {
        return clientRepository.findAll();
    }

    @QueryMapping
    public Client client(@Argument Integer id) {
        return clientRepository.findById(id).orElse(null);
    }

    @MutationMapping
    @Transactional
    public Client createClient(@Argument String name, @Argument String email, @Argument String phoneNumber) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        client.setPhoneNumber(phoneNumber);
        return clientRepository.save(client);
    }

    @MutationMapping
    @Transactional
    public Client updateClient(@Argument Integer id, @Argument String name,
                               @Argument String email, @Argument String phoneNumber) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));

        if (name != null) client.setName(name);
        if (email != null) client.setEmail(email);
        if (phoneNumber != null) client.setPhoneNumber(phoneNumber);

        return clientRepository.save(client);
    }

    @MutationMapping
    @Transactional
    public boolean deleteClient(@Argument Integer id) {
        if (!clientRepository.existsById(id)) return false;
        clientRepository.deleteById(id);
        return true;
    }

    @SchemaMapping(typeName = "Client", field = "projects")
    public List<Project> getProjects(Client client) {
        return projectRepository.findAllByClient_Id(client.getId());
    }
}
