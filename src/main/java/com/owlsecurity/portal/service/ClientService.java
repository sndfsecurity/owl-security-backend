package com.owlsecurity.portal.service;

import java.util.List;
import com.owlsecurity.portal.dto.ClientRequest;
import com.owlsecurity.portal.entity.Client;

public interface ClientService {

    Client saveClient(Client client);

    List<Client> getAllClients();
    
    Client getClientById(Long id);

    
    Client updateClient(Long id, ClientRequest request);
    
    void deleteClient(Long id);
    
    Client getClientByUserId(Long userId);
    
    
}