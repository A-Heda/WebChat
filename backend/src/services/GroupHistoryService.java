package services;

import model.GroupHistory;
import repositories.GroupHistoryRepository;

import java.util.List;

public class GroupHistoryService {

    private GroupHistoryRepository repository;

    public GroupHistoryService() {

        repository =
                new GroupHistoryRepository();

    }

    public void saveHistory(

            String groupId,
            String messageId,
            String senderId,
            String text,
            String action

    ) {

        repository.saveHistory(

                new GroupHistory(

                        groupId,
                        messageId,
                        senderId,
                        text,
                        action,
                        System.currentTimeMillis()

                )

        );

    }

    public List<GroupHistory> getHistory(String groupId){

        return repository.getHistory(groupId);

    }

}
