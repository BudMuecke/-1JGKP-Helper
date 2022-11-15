package de.jgkp.financeBot.service;

import de.jgkp.financeBot.db.entities.Accountupdates;
import de.jgkp.financeBot.db.repositories.AccountupdatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountupdatesService {

    private Services services;
    private AccountupdatesRepository accountupdatesRepository;

    @Autowired
    public AccountupdatesService(Services services, AccountupdatesRepository accountupdatesRepository) {
        this.services = services;
        this.accountupdatesRepository = accountupdatesRepository;
    }

    public void setCurrentDatabaseDate(){
        Accountupdates accountupdates = accountupdatesRepository.findAccountupdatesById(1L);
        accountupdates.setLastAccountupdate(String.valueOf(services.getCurrentDate()));
        accountupdatesRepository.save(accountupdates);
    }

    public String getCurrentDatabaseDate(){
        Accountupdates accountupdates = accountupdatesRepository.findAccountupdatesById(1L);
        return String.valueOf(accountupdates.getLastAccountupdate());
    }
}
