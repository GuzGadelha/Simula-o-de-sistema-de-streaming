public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        String[] categorias = {"Ação", "Ficção Científica", "Drama", "Terror", "Comédia", "Suspense"};
        
        // 1. Inserir 1000 filmes no servidor.
        for (int i = 1; i <= 1000; i++) {
            String cat = categorias[i % categorias.length];
            server.addMovie(new Movie(i, "Filme " + i, cat, 2000 + (i % 25), "Sinopse do filme " + i));
        }

        // 2. Configurar 3 Clientes.
        Client[] clients = {
            new Client("Alice", server),
            new Client("Bob", server),
            new Client("Charlie", server)
        };

        // 3. Pré-carregar 50 filmes no cache (simulando histórico).
        for (Client c : clients) {
            for (int i = 1; i <= 50; i++) {
                c.cache.put(i, new Movie(i, "Filme " + i, categorias[i % categorias.length], 2020, "Sinopse"));
            }
        }

        // 4. Executar bateria de 20 consultas por cliente.
        for (Client c : clients) {
            System.out.println("\n==================================");
            System.out.println("Iniciando bateria para: " + c.name);
            
            // 2 Consultas Inválidas (IDs ou Títulos inexistentes)
            // Utilizando o metodo requestByName para atender ao requisito de tradução de catálogo
            c.requestByName("Filme 9999", true);
            c.requestByName("Filme Inexistente", true);
            
            // 6 Consultas Cache Hit (IDs 1 a 6 que foram pré-carregados).
            for(int i=1; i<=6; i++) c.requestByName("Filme " + i, true);
            
            // 6 Consultas sem indexação (Busca Sequencial no DB para IDs não cacheados).
            for(int i=100; i<=105; i++) c.requestByName("Filme " + i, false);
            
            // 6 Consultas com indexação (Tabela Hash do DB para IDs não cacheados).
            for (int i = 200; i <= 205; i++) c.requestByName("Filme " + i, true);
        }

        System.out.println("\n======== ANÁLISE FINAL ========");
        Client target = clients[0];
        
        // Exibe os 10 filmes mais recentemente utilizados
        target.cache.printTop10();
        
        // Mostrar quais registros foram removidos do cache devido à política LRU
        System.out.println("\nRemovidos pela política LRU (Cliente 1): " + target.cache.evicted.size() + " itens.");
        CustomListNode currEvicted = target.cache.evicted.head;
        while(currEvicted != null) {
            Movie m = (Movie) currEvicted.val;
            System.out.println(" - " + m.title + " (ID: " + m.id + ")");
            currEvicted = currEvicted.next;
        }
        
        // Árvore splay de preferências do cliente
        System.out.println("\nTop 5 Preferências / Mais Acessados (Cliente 1):");
        System.out.println(" (Raiz da árvore representa o interesse principal ou mais recente)");
        CustomList topPrefs = target.preferencesTree.getTopN(5);
        CustomListNode currPref = topPrefs.head;
        int rankPref = 1;
        while(currPref != null) {
            System.out.println(" " + rankPref + "º: " + currPref.val);
            currPref = currPref.next;
            rankPref++;
        }

        // Árvore splay de popularidade do servidor
        System.out.println("\nTop 10 Popularidade Global (Servidor):");
        CustomList topPop = server.popularityTree.getTopN(10);
        CustomListNode currPop = topPop.head;
        int rankPop = 1;
        while(currPop != null) {
            System.out.println(" " + rankPop + "º: " + currPop.val);
            currPop = currPop.next;
            rankPop++;
        }
    }
}