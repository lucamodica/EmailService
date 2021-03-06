package com.projprogiii.servermail.server.session.command;

import com.projprogiii.lib.enums.ServerResponseName;
import com.projprogiii.lib.objects.ClientRequest;
import com.projprogiii.lib.objects.Email;
import com.projprogiii.lib.objects.ServerResponse;
import com.projprogiii.servermail.ServerApp;
import com.projprogiii.servermail.model.log.LogType;
import javafx.application.Platform;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FetchEmail extends Command{

    @Override
    public ServerResponse handle(ClientRequest req){
        ServerResponseName name;
        List<Email> emails;
        ReentrantReadWriteLock.ReadLock readLock =
                syncManager.getLock(req.auth()).readLock();

        readLock.lock();
        emails = ServerApp.model.getDbManager()
                .retrieveEmails(req.auth())
                .stream()
                .toList();
        readLock.unlock();

        name = (emails != null) ?
                ServerResponseName.SUCCESS :
                ServerResponseName.OP_ERROR;

        printCommandLog(req, name);
        return new ServerResponse(name, emails);
    }

    protected void printCommandLog(ClientRequest req, ServerResponseName name){
        switch(name){
            case SUCCESS -> Platform.runLater(() -> logManager.printLog(
                    "Email for " + req.auth() +
                            " successfully fetched!", LogType.SYSOP));

            case OP_ERROR -> Platform.runLater(() -> logManager.printError(
                    "ERROR (" + req.cmdName().toString() + " for "
                            + req.auth() + "): operation failed!"));
        }
    }
}
