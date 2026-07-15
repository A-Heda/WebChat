package repositories;

import model.BlockedUser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BlockRepository {

    private static final String FILE = "backend/database/blocked_users.txt";

    public List<BlockedUser> loadAll() {

        List<BlockedUser> list = new ArrayList<>();

        File file = new File(FILE);

        if (!file.exists())
            return list;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\|");

                if (parts.length != 2)
                    continue;

                list.add(
                        new BlockedUser(
                                parts[0],
                                parts[1]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void saveAll(List<BlockedUser> list) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {

            for (BlockedUser block : list) {

                writer.write(
                        block.getOwnerId()
                                + "|"
                                + block.getBlockedId());

                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void block(String ownerId, String blockedId) {

        List<BlockedUser> list = loadAll();

        for (BlockedUser b : list) {

            if (b.getOwnerId().equals(ownerId)
                    &&
                    b.getBlockedId().equals(blockedId))
                return;
        }

        list.add(new BlockedUser(ownerId, blockedId));

        saveAll(list);
    }

    public void unblock(String ownerId, String blockedId) {

        List<BlockedUser> list = loadAll();

        list.removeIf(b ->

        b.getOwnerId().equals(ownerId)
                &&
                b.getBlockedId().equals(blockedId)

        );

        saveAll(list);
    }

    public boolean isBlocked(String user1, String user2) {

        List<BlockedUser> list = loadAll();

        for (BlockedUser b : list) {

            if ((b.getOwnerId().equals(user1)
                    &&
                    b.getBlockedId().equals(user2))

                    ||

                    (b.getOwnerId().equals(user2)
                            &&
                            b.getBlockedId().equals(user1))) {

                return true;
            }
        }

        return false;
    }

}