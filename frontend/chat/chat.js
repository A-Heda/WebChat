let currentChatId = null;
let isDarkMode = false;

// ─── Special Folders ───────────────────────────────────────────

function openSavedMessages() {
    currentChatId = '__saved__';
    clearActiveChatItems();

    const win = document.getElementById('chat-window');
    win.innerHTML = `
        <div class="chat-header">
            <div class="folder-icon saved" style="width:38px;height:38px;font-size:15px;border-radius:50%;">
                <i class="fa-solid fa-bookmark"></i>
            </div>
            <span class="chat-name">Saved Messages</span>
            <div class="chat-header-actions">
                <button class="icon-btn" title="Pin a message" onclick="togglePin('__saved__')">
                    <i class="fa-solid fa-thumbtack"></i>
                </button>
            </div>
        </div>
        <div class="messages-area" id="messages-area">
            <p style="text-align:center;color:#94a3b8;font-size:13px;">
                Your saved notes and messages appear here.
            </p>
        </div>
        <div class="message-input-area">
            <input type="text" id="msg-input" placeholder="Save a note..." onkeydown="handleKey(event)">
            <button class="send-btn" onclick="sendMessage()">
                <i class="fa-solid fa-paper-plane"></i>
            </button>
        </div>
    `;
    // TODO: fetch saved messages from API GET /saved-messages
}

function openArchive() {
    currentChatId = '__archive__';
    clearActiveChatItems();

    const win = document.getElementById('chat-window');
    win.innerHTML = `
        <div class="chat-header">
            <div class="folder-icon archive" style="width:38px;height:38px;font-size:15px;border-radius:50%;">
                <i class="fa-solid fa-box-archive"></i>
            </div>
            <span class="chat-name">Archive</span>
        </div>
        <div class="messages-area" id="archive-list" style="gap:0;padding:12px;">
            <p style="text-align:center;color:#94a3b8;font-size:13px;margin-top:30px;">
                Archived chats will appear here.
            </p>
        </div>
    `;
    // TODO: fetch archived chats from API GET /chats?archived=true
}

function clearActiveChatItems() {
    document.querySelectorAll('.chat-item').forEach(i => i.classList.remove('active'));
}

// ─── Sidebar Search ─────────────────────────────────────────────

function searchChats() {
    const q = document.getElementById('search-input').value.toLowerCase();
    document.querySelectorAll('.chat-item').forEach(item => {
        item.style.display = item.dataset.name.toLowerCase().includes(q) ? '' : 'none';
    });
}

// ─── New Chat / Add Contact ─────────────────────────────────────

function openNewChat() {
    openModal('add-contact-modal');
}

function addContact() {
    const userId = document.getElementById('contact-id-input').value.trim();
    if (!userId) return;
    // TODO: call API POST /contacts with { userId }
    closeModal('add-contact-modal');
    document.getElementById('contact-id-input').value = '';
}

// ─── Chat Selection ─────────────────────────────────────────────

function selectChat(chatId, name, avatarSrc) {
    currentChatId = chatId;
    clearActiveChatItems();
    document.querySelector(`.chat-item[data-id="${chatId}"]`)?.classList.add('active');

    const win = document.getElementById('chat-window');
    win.innerHTML = `
        <div class="chat-header">
            <img src="${avatarSrc}" class="avatar" alt="">
            <span class="chat-name">${name}</span>
            <div class="chat-header-actions">
                <button class="icon-btn" title="Archive chat" onclick="archiveChat('${chatId}')">
                    <i class="fa-solid fa-box-archive"></i>
                </button>
                <button class="icon-btn" title="Pin chat" onclick="togglePin('${chatId}')">
                    <i class="fa-solid fa-thumbtack"></i>
                </button>
            </div>
        </div>
        <div class="messages-area" id="messages-area">
            <p style="text-align:center;color:#94a3b8;font-size:13px;">No messages yet.</p>
        </div>
        <div class="message-input-area">
            <input type="text" id="msg-input" placeholder="Type a message..." onkeydown="handleKey(event)">
            <button class="send-btn" onclick="sendMessage()">
                <i class="fa-solid fa-paper-plane"></i>
            </button>
        </div>
    `;
    // TODO: fetch messages from API GET /messages?chatId=...
}

// ─── Messaging ──────────────────────────────────────────────────

function sendMessage() {
    const input = document.getElementById('msg-input');
    const text = input.value.trim();
    if (!text || !currentChatId) return;

    const type = currentChatId === '__saved__' ? 'note' : 'sent';
    appendMessage(text, type);
    input.value = '';
    // TODO: send via WebSocket or API POST /messages
}

function handleKey(e) {
    if (e.key === 'Enter') sendMessage();
}

function appendMessage(text, type) {
    const area = document.getElementById('messages-area');
    if (!area) return;
    const emptyMsg = area.querySelector('p');
    if (emptyMsg) emptyMsg.remove();

    const div = document.createElement('div');
    div.className = `message ${type}`;
    const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    div.innerHTML = `${text}<div class="msg-time">${time}</div>`;
    area.appendChild(div);
    area.scrollTop = area.scrollHeight;
}

// ─── Pin & Archive ──────────────────────────────────────────────

function togglePin(chatId) {
    const item = document.querySelector(`.chat-item[data-id="${chatId}"]`);
    if (!item) return;

    const isPinned = item.dataset.pinned === 'true';
    item.dataset.pinned = isPinned ? 'false' : 'true';

    // move pinned items to top of list
    const list = document.getElementById('chat-list');
    if (!isPinned) {
        const pin = document.createElement('span');
        pin.className = 'pin-indicator';
        pin.innerHTML = '<i class="fa-solid fa-thumbtack" style="color:#6366f1;font-size:11px;"></i>';
        pin.style.cssText = 'position:absolute;top:6px;right:8px;';
        item.style.position = 'relative';
        item.appendChild(pin);
        list.prepend(item);
    } else {
        item.querySelector('.pin-indicator')?.remove();
        item.style.position = '';
    }
    // TODO: call API PATCH /chats/:chatId with { pinned: !isPinned }
}

function archiveChat(chatId) {
    const item = document.querySelector(`.chat-item[data-id="${chatId}"]`);
    if (!item) return;

    item.style.transition = 'opacity 0.3s';
    item.style.opacity = '0';
    setTimeout(() => {
        item.remove();
        // show feedback
        showToast('Chat archived');
    }, 300);
    // TODO: call API PATCH /chats/:chatId with { archived: true }

    // go back to empty state
    document.getElementById('chat-window').innerHTML = `
        <div class="empty-state">
            <i class="fa-solid fa-comments" style="font-size:48px;color:#334155;margin-bottom:16px;"></i>
            <p>Select a chat to start messaging</p>
        </div>
    `;
    currentChatId = null;
}

// ─── Toast Notification ─────────────────────────────────────────

function showToast(message) {
    const existing = document.querySelector('.toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        bottom: 24px;
        left: 50%;
        transform: translateX(-50%);
        background: #6366f1;
        color: white;
        padding: 10px 20px;
        border-radius: 8px;
        font-size: 13px;
        z-index: 9999;
        opacity: 0;
        transition: opacity 0.3s;
    `;
    document.body.appendChild(toast);
    requestAnimationFrame(() => toast.style.opacity = '1');
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 2500);
}

// ─── Modal Helpers ──────────────────────────────────────────────

function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.style.display = 'flex';
        requestAnimationFrame(() => modal.classList.add('show'));
    }
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) {
        modal.classList.remove('show');
        setTimeout(() => modal.style.display = 'none', 200);
    }
}

// close modal when clicking backdrop
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        closeModal(e.target.id);
    }
});

// ─── Settings ───────────────────────────────────────────────────

function openSettings() {
    openModal('settings-modal');
}

function toggleDarkMode() {
    isDarkMode = !isDarkMode;
    document.body.classList.toggle('dark', isDarkMode);

    const toggle = document.getElementById('dark-mode-toggle');
    const icon = document.getElementById('dark-mode-icon');
    const label = document.getElementById('dark-mode-label');

    if (toggle) toggle.classList.toggle('on', isDarkMode);
    if (icon) icon.className = isDarkMode ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
    if (label) label.textContent = isDarkMode ? 'Light Mode' : 'Dark Mode';
}

function logout() {
    if (confirm('Are you sure you want to logout?')) {
        // TODO: call API POST /logout
        window.location.href = '../login/login.html';
    }
}

function deleteAccount() {
    if (confirm('Delete your account? This cannot be undone.')) {
        // TODO: call API DELETE /account
        window.location.href = '../signup/signup.html';
    }
}

// ─── Profile Edit ────────────────────────────────────────────────

function openEditProfile() {
    closeModal('settings-modal');
    openModal('edit-profile-modal');
}

function saveName() {
    const name = document.getElementById('new-name-input').value.trim();
    if (!name) return;
    document.getElementById('sidebar-username').textContent = name;
    showToast('Name updated');
    // TODO: call API PATCH /profile with { name }
}

function saveUserId() {
    const uid = document.getElementById('new-userid-input').value.trim();
    if (!uid) return;
    document.getElementById('sidebar-userid').textContent = '@' + uid;
    showToast('User ID updated');
    // TODO: call API PATCH /profile with { userId: uid }
}

function openChangePhoto() {
    document.getElementById('photo-upload-input').click();
}

function handlePhotoUpload(input) {
    const file = input.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('sidebar-avatar').src = e.target.result;
        showToast('Photo updated');
        // TODO: upload file to API POST /profile/photo
    };
    reader.readAsDataURL(file);
}
