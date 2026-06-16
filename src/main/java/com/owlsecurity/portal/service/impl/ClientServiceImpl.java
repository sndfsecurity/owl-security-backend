package com.owlsecurity.portal.service.impl;

import java.time.LocalDateTime;

import java.util.List;
import org.springframework.stereotype.Service;

import com.owlsecurity.portal.dto.ClientRequest;
import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.service.ClientService;

import com.owlsecurity.portal.repository.UserRepository;

import com.owlsecurity.portal.entity.User;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    
    private final UserRepository userRepository;

    public ClientServiceImpl(
            ClientRepository clientRepository,
            UserRepository userRepository
    ) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }
    
    
    @Override
    public Client getClientByUserId(Long userId) {

        return clientRepository
                .findByUserId(userId)
                .orElse(null);
    }
    
    
    @Override
    public Client saveClient(Client client) {

        client.setCreatedAt(
                LocalDateTime.now()
        );

        return clientRepository.save(client);
    }

    @Override
    public List<Client> getAllClients() {

        return clientRepository.findAll();
    }
    
    @Override
    public Client getClientById(Long id) {

        return clientRepository.findById(id)
                .orElse(null);
    }
    
   // update
    @Override
    public Client updateClient(
            Long id,
            ClientRequest request
    ) {

        Client client =
                clientRepository.findById(id)
                        .orElse(null);

        if (client == null) {
            return null;
        }

        client.setCompanyName(
                request.getCompanyName()
        );

        client.setContactPerson(
                request.getContactPerson()
        );

        client.setEmail(
                request.getEmail()
        );

        client.setPhone(
                request.getPhone()
        );

        client.setStatus(
                request.getStatus()
        );

        client.setUserId(
                request.getUserId()
        );

        Client updatedClient =
                clientRepository.save(client);

        if (client.getUserId() != null) {

            User user =
                    userRepository.findById(
                            client.getUserId()
                    ).orElse(null);

            if (user != null) {

                user.setName(
                        request.getCompanyName()
                );

                user.setEmail(
                        request.getEmail()
                );

                userRepository.save(user);
            }
        }

        return updatedClient;
    }
    
    
     //delete
  
    @Override
    public void deleteClient(Long id) {

        Client client =
                clientRepository.findById(id)
                        .orElse(null);

        if (client == null) {
            return;
        }

        Long userId = client.getUserId();

        clientRepository.deleteById(id);

        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }
}