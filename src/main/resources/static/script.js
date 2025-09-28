let adminMode = false; // Modalità admin per mostrare il cestino

// Carica i post dal backend
function loadPosts() {
    fetch('http://localhost:8080/api/posts/latest')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(posts => {
            displayPosts(posts);
        })
        .catch(error => {
            console.error('Errore nel caricamento dei post:', error);
            displayFallbackPost();
        });
}

// Prompt per la password admin - CON DEBUG COMPLETO
async function askPassword() {
    const password = prompt("Inserisci la password:");
    if (!password) return;

    try {
        const res = await fetch('/api/check-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({ password: password })
        });

        console.log("📡 Status risposta:", res.status);

        if (!res.ok) {
            const errorText = await res.text();
            alert(`Errore server: ${res.status}`);
            return;
        }

        const data = await res.json();
        console.log("📋 Dati ricevuti:", data);

        if (data.success === true) {
            alert("✅ Password corretta!");
            adminMode = true;
            updateAdminUI(); // Aggiorna l'interfaccia admin
            loadPosts(); // ricarica i post mostrando cestino
        } else {
            alert("❌ Password errata!");
        }
    } catch (error) {
        alert("❌ Errore di connessione: " + error.message);
    }
}

// Funzione per aggiornare l'interfaccia admin
function updateAdminUI() {
    const addPostBtn = document.getElementById('add-post-btn');
    
    if (adminMode) {
        if (addPostBtn) {
            addPostBtn.style.display = "block";
            console.log("🔧 Pulsante 'Aggiungi Post' mostrato");
        }
    } else {
        if (addPostBtn) {
            addPostBtn.style.display = "none";
            console.log("🔧 Pulsante 'Aggiungi Post' nascosto");
        }
    }
}

// Mostra i post nella pagina
function displayPosts(posts) {
    const container = document.getElementById('posts-container');
    if (!container) return;

    console.log("📝 Rendering posts, adminMode:", adminMode);
    container.innerHTML = '';

    if (!posts || posts.length === 0) {
        container.innerHTML = '<div class="loading">Nessun post disponibile</div>';
        return;
    }

    posts.forEach((post, index) => {
        console.log(`📝 Rendering post ${index + 1}, adminMode: ${adminMode}`);

        const postDiv = document.createElement('div');
        postDiv.className = 'latest-post-content';

        // Contenuto principale del post
        postDiv.innerHTML = `
            <h3 class="name-post">${escapeHtml(post.title)}</h3>
            <div class="post-meta">
                <h4>Data pubblicazione</h4>
                <span>${formatDate(post.createdAt)}</span>
            </div>
            <p>${escapeHtml(post.summary || (post.content ? post.content.substring(0, 200) + '...' : 'Contenuto non disponibile'))}</p>
            ${post.viewCount !== null && post.viewCount !== undefined ? `<div class="post-views">Visualizzazioni: ${post.viewCount}</div>` : ''}
        `;

        // Mostra il cestino solo se adminMode è true
        if (adminMode) {
            console.log("🗑️ Aggiungendo cestino al post:", post.title);
            const trashBtn = document.createElement('button');
            trashBtn.className = 'trash-btn';
            trashBtn.textContent = "🗑️";
            trashBtn.onclick = (e) => {
                e.stopPropagation();
                if (confirm(`Vuoi davvero eliminare "${post.title}"?`)) {
                    deletePost(post.id);
                }
            };
            postDiv.appendChild(trashBtn);
        }

        // Click sul post
        postDiv.addEventListener('click', () => {
            openPost(post.id);
        });

        container.appendChild(postDiv);
    });

    // Aggiorna l'UI admin dopo aver renderizzato i post
    updateAdminUI();
}

// Mostra un fallback con dati statici se il backend non risponde
function displayFallbackPost() {
    const posts = [
        {
            id: 1,
            title: "Primo Post del Blog",
            createdAt: new Date().toISOString(),
            content: "Il primo post del mio blog personale",
            viewCount: 0
        },
        {
            id: 2,
            title: "Secondo Post",
            createdAt: new Date().toISOString(),
            content: "Riflessioni sul mio percorso",
            viewCount: 0
        }
    ];

    displayPosts(posts);
}

// Funzione per eliminare un post
function deletePost(postId) {
    if (confirm('Sei sicuro di voler eliminare questo post?')) {
        console.log('Eliminazione post:', postId);
        // In un caso reale qui faresti fetch DELETE verso il backend
        loadPosts(); // ricarica i post dopo l'eliminazione
    }
}

// Funzione per aggiungere un post (placeholder)
function addPost() {
    alert("Funzione 'Aggiungi Post' non ancora implementata!");
    // Qui implementerai la logica per aggiungere un nuovo post
}

// Format della data in formato italiano
function formatDate(dateString) {
    if (!dateString) return 'Data non disponibile';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('it-IT');
    } catch (error) {
        return 'Data non valida';
    }
}

// Escape HTML per sicurezza
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Apri post (puoi aggiungere navigazione reale)
function openPost(postId) {
    console.log('Apri post:', postId);
}

// Avvia quando la pagina è pronta
document.addEventListener('DOMContentLoaded', () => {
    console.log("🚀 Pagina caricata, adminMode iniziale:", adminMode);
    
    // Nascondi il pulsante all'inizio
    updateAdminUI();
    
    // Carica i post
    loadPosts();
});