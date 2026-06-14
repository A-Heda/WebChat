Repository classes make connection with database And do the actions write and read,
Databases right now are users.txt & groups.txt & chats/ & media/. For chats we will have a unique text file for each chat either a groupchat or a private one.

For now the format of users to save in database users.txt is like --> id|username|password|profileImagePath
For now the format of groups to save in database groups.txt is like --> groupId|groupName|adminId|memberIds
