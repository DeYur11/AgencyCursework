package org.example.advertisingagency.service.user;

import org.example.advertisingagency.model.Client;
import org.example.advertisingagency.model.Project;
import org.example.advertisingagency.repository.ClientRepository;
import org.example.advertisingagency.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;

    public ClientService(ClientRepository clientRepository,
                         ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findOne(Integer id) {
        return clientRepository.findById(id);
    }

    public Client create(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> update(Integer id, Client updated) {
        return clientRepository.findById(id).map(client -> {
            client.setName(updated.getName());
            client.setEmail(updated.getEmail());
            client.setPhoneNumber(updated.getPhoneNumber());
            client.setUpdateDatetime(updated.getUpdateDatetime());
            return clientRepository.save(client);
        });
    }

    public boolean delete(Integer id) {
        return clientRepository.findById(id).map(client -> {
            clientRepository.delete(client);
            return true;
        }).orElse(false);
    }

    public List<Project> getProjects(Integer clientId) {
        return projectRepository.findAllByClient_Id(clientId);
    }
}
