class Server {
    LinkedListDB database;
    HashTableDB index;
    SplayTree popularityTree;

    public Server() {
        database = new LinkedListDB();
        index = new HashTableDB(2000);
        popularityTree = new SplayTree();
    }

    public void addMovie(Movie m) {
        database.add(m);
        index.put(m.id, database.head); // Indexação na Tabela Hash apontando para a Lista
    }
    
    // Simula a consulta ao catálogo simplificado para converter nome em ID
    public int resolveTitleToId(String title) {
        NodeLL current = database.head;
        while (current != null) {
            // Contabiliza apenas se quisermos, mas como é só catálogo simplificado, não afeta a busca profunda
            if (current.movie.title.equals(title)) {
                return current.movie.id;
            }
            current = current.next;
        }
        return -1;
    }

    public Movie requestMovie(int id, boolean useIndex) {
        Movie found;
        if (useIndex) {
            found = index.get(id);
            System.out.println("  [Servidor] Comparações com indexação (Hash): " + index.comparisons);
        } else {
            found = database.searchSequential(id);
            System.out.println("  [Servidor] Comparações sem indexação (Seq): " + database.comparisons);
        }

        if (found != null) {
            popularityTree.insertOrAccess(found.id, found.title); // Atualiza árvore splay do servidor
        }
        return found;
    }
}

class Client {
    String name;
    LRUCache cache;
    SplayTree preferencesTree;
    Server serverRef;
    HuffmanCompressor huffman;

    public Client(String name, Server serverRef) {
        this.name = name;
        this.cache = new LRUCache(50);
        this.preferencesTree = new SplayTree();
        this.serverRef = serverRef;
        this.huffman = new HuffmanCompressor();
    }
    
    public void requestByName(String title, boolean useIndexServer) {
        System.out.println("\nCliente " + name + " requisitando filme pelo nome: " + title);
        System.out.println("  [Catálogo] Resolvendo Nome para ID...");
        int id = serverRef.resolveTitleToId(title);
        
        if (id == -1) {
            System.out.println("  [Erro] Filme '" + title + "' não existe no catálogo mestre (Consulta Inválida).");
            return;
        }
        
        request(id, useIndexServer);
    }

    public void request(int id, boolean useIndexServer) {
        System.out.println("\nCliente " + name + " requisitando ID: " + id);
        
        // Verificação de cache local (Hash + LRU)
        Movie m = cache.get(id);
        if (m != null) {
            System.out.println("  [Cache HIT] Filme " + m.title + " encontrado localmente.");
            preferencesTree.insertOrAccess(m.id, m.category);
            return;
        }
        
        System.out.println("  [Cache MISS] Buscando no servidor...");
        String msg = "GET /filme/" + id;
        String compressed = huffman.compress(msg);
        
        System.out.println("  [Rede] Msg original: " + msg.length() * 8 + " bits | Comprimida: " + compressed.length() + " bits");
        System.out.println("  [Rede] Taxa de compressão: " + String.format("%.2f", (100 - (compressed.length() * 100.0 / (msg.length() * 8)))) + "%");

        m = serverRef.requestMovie(id, useIndexServer);
        if (m != null) {
            System.out.println("  [Sucesso] Filme recebido: " + m.title);
            cache.put(m.id, m);
            preferencesTree.insertOrAccess(m.id, m.category);
        } else {
            System.out.println("  [Erro] Filme não encontrado (Consulta Inválida de ID).");
        }
    }
}