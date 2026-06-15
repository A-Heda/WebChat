Repository classes make connection with database And do the actions write and read,
Databases right now are users.txt & groups.txt & chats/ & media/. For chats we will have a unique text file for each chat either a groupchat or a private one.

For now the format of users to save in database users.txt is like --> id|username|password|profileImagePath
For now the format of groups to save in database groups.txt is like --> groupId|groupName|adminId|memberIds
The format of private chat saved in data base --> messageId|senderId|text|timestamp|edited|deleted|chatId
The format of group chat saves in data base --> 


The path to save a chat messages --> database/chats/chatId.txt
That chatId format for privateChats is like --> private_user1Id_user2Id ... in which always the smaller number comes first. private_1_2(true)    private_2_1(not our format) 
chatId format for privateChats is like --> group_groupId  e.g. group_1