class NodeLL {
    Movie movie;
    NodeLL next;
    public NodeLL(Movie m) { this.movie = m; this.next = null; }
}

class LinkedListDB {
    NodeLL head;
    int comparisons = 0;

    public void add(Movie m) {
        NodeLL newNode = new NodeLL(m);
        newNode.next = head;
        head = newNode;
    }

    // Busca sequencial O(n)
    public Movie searchSequential(int id) {
        comparisons = 0;
        NodeLL current = head;
        while (current != null) {
            comparisons++;
            if (current.movie.id == id) return current.movie;
            current = current.next;
        }
        return null;
    }
}

class HashNode {
    int key;
    NodeLL dbRef;
    HashNode next;
    public HashNode(int key, NodeLL dbRef) { this.key = key; this.dbRef = dbRef; }
}

class HashTableDB {
    private HashNode[] table;
    private int capacity;
    public int comparisons = 0;

    public HashTableDB(int capacity) {
        this.capacity = capacity;
        table = new HashNode[capacity];
    }

    public void put(int key, NodeLL dbRef) {
        int index = key % capacity;
        HashNode newNode = new HashNode(key, dbRef);
        newNode.next = table[index];
        table[index] = newNode;
    }

    public Movie get(int key) {
        comparisons = 0;
        int index = key % capacity;
        HashNode current = table[index];
        HashNode prev = null;
        
        while (current != null) {
            comparisons++;
            if (current.key == key) {
                // Move-to-Front para otimização de acessos futuros (Autoajuste em colisão)
                if (prev != null) {
                    prev.next = current.next;
                    current.next = table[index];
                    table[index] = current;
                }
                return current.dbRef.movie;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }
}


class CustomQueueNode {
    SplayNode splayNode;
    CustomQueueNode next;
    public CustomQueueNode(SplayNode sn) { this.splayNode = sn; }
}

class CustomQueue {
    CustomQueueNode head, tail;
    public void enqueue(SplayNode sn) {
        CustomQueueNode n = new CustomQueueNode(sn);
        if (tail != null) tail.next = n;
        tail = n;
        if (head == null) head = tail;
    }
    public SplayNode dequeue() {
        if (head == null) return null;
        SplayNode sn = head.splayNode;
        head = head.next;
        if (head == null) tail = null;
        return sn;
    }
    public boolean isEmpty() { return head == null; }
}

class CustomListNode {
    Object val;
    CustomListNode next;
    public CustomListNode(Object v) { this.val = v; }
}

class CustomList {
    CustomListNode head, tail;
    int size = 0;
    public void add(Object v) {
        CustomListNode n = new CustomListNode(v);
        if (tail != null) tail.next = n;
        tail = n;
        if (head == null) head = tail;
        size++;
    }
    public int size() { return size; }
}

class IntToLRUNodeMapEntry {
    int key;
    LRUNode value;
    IntToLRUNodeMapEntry next;
    public IntToLRUNodeMapEntry(int k, LRUNode v) { key = k; value = v; }
}

class IntToLRUNodeMap {
    private IntToLRUNodeMapEntry[] table;
    private int capacity;
    private int size;
    public IntToLRUNodeMap(int cap) {
        table = new IntToLRUNodeMapEntry[cap];
        capacity = cap;
    }
    public void put(int key, LRUNode val) {
        int idx = key % capacity;
        IntToLRUNodeMapEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) { curr.value = val; return; }
            curr = curr.next;
        }
        IntToLRUNodeMapEntry n = new IntToLRUNodeMapEntry(key, val);
        n.next = table[idx];
        table[idx] = n;
        size++;
    }
    public LRUNode get(int key) {
        int idx = key % capacity;
        IntToLRUNodeMapEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) return curr.value;
            curr = curr.next;
        }
        return null;
    }
    public boolean containsKey(int key) { return get(key) != null; }
    public void remove(int key) {
        int idx = key % capacity;
        IntToLRUNodeMapEntry curr = table[idx];
        IntToLRUNodeMapEntry prev = null;
        while (curr != null) {
            if (curr.key == key) {
                if (prev == null) table[idx] = curr.next;
                else prev.next = curr.next;
                size--;
                return;
            }
            prev = curr;
            curr = curr.next;
        }
    }
    public int size() { return size; }
}

// Nó para a lista duplamente encadeada do LRU
class LRUNode {
    int key;
    Movie movie;
    LRUNode prev, next;
    public LRUNode(int key, Movie movie) { this.key = key; this.movie = movie; }
}

class LRUCache {
    private int capacity;
    private IntToLRUNodeMap map; // Cache map customizado
    private LRUNode head, tail;
    public CustomList evicted; // Lista customizada de expulsos

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new IntToLRUNodeMap(capacity * 2);
        this.evicted = new CustomList();
    }

    public Movie get(int key) {
        if (map.containsKey(key)) {
            LRUNode node = map.get(key);
            remove(node);
            setHead(node);
            return node.movie;
        }
        return null;
    }

    public void put(int key, Movie movie) {
        if (map.containsKey(key)) {
            LRUNode old = map.get(key);
            old.movie = movie;
            remove(old);
            setHead(old);
        } else {
            LRUNode created = new LRUNode(key, movie);
            if (map.size() >= capacity) {
                map.remove(tail.key);
                evicted.add(tail.movie); // Registrando remoção pela política LRU.
                remove(tail);
            }
            setHead(created);
            map.put(key, created);
        }
    }

    private void remove(LRUNode node) {
        if (node.prev != null) node.prev.next = node.next;
        else head = node.next;
        if (node.next != null) node.next.prev = node.prev;
        else tail = node.prev;
    }

    private void setHead(LRUNode node) {
        node.next = head;
        node.prev = null;
        if (head != null) head.prev = node;
        head = node;
        if (tail == null) tail = head;
    }
    
    public void printTop10() {
        System.out.println("Top 10 itens recentes no Cache:");
        LRUNode curr = head;
        int count = 0;
        while(curr != null && count < 10) {
            System.out.println("- " + curr.movie.title);
            curr = curr.next;
            count++;
        }
    }
}

// Árvore Splay para Preferências e Popularidade.
class SplayNode {
    int key;
    String label;
    SplayNode left, right, parent;
    public SplayNode(int key, String label) { this.key = key; this.label = label; }
}

class SplayTree {
    SplayNode root;

    private void rightRotate(SplayNode x) {
        SplayNode y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    private void leftRotate(SplayNode x) {
        SplayNode y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void splay(SplayNode x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                if (x == x.parent.left) rightRotate(x.parent);
                else leftRotate(x.parent);
            } else if (x == x.parent.left && x.parent == x.parent.parent.left) {
                rightRotate(x.parent.parent);
                rightRotate(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.right) {
                leftRotate(x.parent.parent);
                leftRotate(x.parent);
            } else if (x == x.parent.right && x.parent == x.parent.parent.left) {
                leftRotate(x.parent);
                rightRotate(x.parent);
            } else {
                rightRotate(x.parent);
                leftRotate(x.parent);
            }
        }
    }

    public void insertOrAccess(int key, String label) {
        if (root == null) {
            root = new SplayNode(key, label);
            return;
        }
        SplayNode curr = root;
        SplayNode parent = null;
        while (curr != null) {
            parent = curr;
            if (key == curr.key) {
                splay(curr); // Move o nó acessado para a raiz.
                return;
            } else if (key < curr.key) curr = curr.left;
            else curr = curr.right;
        }
        SplayNode newNode = new SplayNode(key, label);
        newNode.parent = parent;
        if (key < parent.key) parent.left = newNode;
        else parent.right = newNode;
        splay(newNode);
    }
    
    // N mais próximos da raiz usando Level Order Traversal (Busca em Largura)
    public CustomList getTopN(int n) {
        CustomList result = new CustomList();
        if (root == null || n <= 0) return result;
        
        CustomQueue queue = new CustomQueue();
        queue.enqueue(root);
        
        while (!queue.isEmpty() && result.size() < n) {
            SplayNode node = queue.dequeue();
            result.add(node.label);
            
            if (node.left != null) queue.enqueue(node.left);
            if (node.right != null) queue.enqueue(node.right);
        }
        return result;
    }
}