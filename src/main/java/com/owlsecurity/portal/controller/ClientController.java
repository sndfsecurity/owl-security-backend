package com.owlsecurity.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.owlsecurity.portal.dto.ClientRequest;
import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.service.ClientService;



@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(
            ClientService clientService
    ) {
        this.clientService = clientService;
    }

    @PostMapping
    public Client createClient(
            @RequestBody ClientRequest request
    ) {

        Client client = new Client();

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
        
        System.out.println(
        	    "USER ID RECEIVED = "
        	    + request.getUserId()
        	);
        
        client.setUserId(
        	    request.getUserId()
        	);

        return clientService.saveClient(client);
    }

    @GetMapping
    public List<Client> getAllClients() {

        return clientService.getAllClients();
    }
    
    
    @GetMapping("/{id}")
    public Client getClientById(
            @PathVariable Long id
    ) {
        return clientService.getClientById(id);
    }
    
    @GetMapping("/user/{userId}")
    public Client getClientByUserId(
            @PathVariable Long userId
    ) {
        return clientService.getClientByUserId(userId);
    }
    
    
    //update
    
    @PutMapping("/{id}")
    public Client updateClient(
            @PathVariable Long id,
            @RequestBody ClientRequest request
    ) {

        return clientService.updateClient(
                id,
                request
        );
    }
    
    
   //delete
    
    
    @DeleteMapping("/{id}")
    public String deleteClient(
            @PathVariable Long id
    ) {

        clientService.deleteClient(id);

        return "Client Deleted Successfully";
    }
}