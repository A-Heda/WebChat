package services;

import repositories.BlockRepository;

public class BlockService {

    private BlockRepository blockRepository;

    public BlockService() {
        blockRepository = new BlockRepository();
    }

    public void blockUser(String ownerId, String blockedId) {
        blockRepository.block(ownerId, blockedId);
    }

    public void unblockUser(String ownerId, String blockedId) {
        blockRepository.unblock(ownerId, blockedId);
    }

    public boolean isBlocked(String user1, String user2) {
        return blockRepository.isBlocked(user1, user2);
    }
}
