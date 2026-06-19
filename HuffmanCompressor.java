// Estruturas de apoio para substituir o HashMap
class CharIntEntry {
    char key;
    int value;
    CharIntEntry next;
    public CharIntEntry(char k, int v) { key = k; value = v; }
}

class CharIntMap {
    private CharIntEntry[] table;
    private int capacity = 256; // Suficiente para caracteres simples
    public CharIntMap() { table = new CharIntEntry[capacity]; }
    
    public void put(char key, int val) {
        int idx = key % capacity;
        CharIntEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) { curr.value = val; return; }
            curr = curr.next;
        }
        CharIntEntry n = new CharIntEntry(key, val);
        n.next = table[idx];
        table[idx] = n;
    }
    
    public int getOrDefault(char key, int def) {
        int idx = key % capacity;
        CharIntEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) return curr.value;
            curr = curr.next;
        }
        return def;
    }
    
    // Método para recuperar todas as chaves (para popular o heap)
    public char[] getKeys() {
        int count = 0;
        for (int i = 0; i < capacity; i++) {
            CharIntEntry curr = table[i];
            while (curr != null) { count++; curr = curr.next; }
        }
        char[] keys = new char[count];
        int j = 0;
        for (int i = 0; i < capacity; i++) {
            CharIntEntry curr = table[i];
            while (curr != null) { keys[j++] = curr.key; curr = curr.next; }
        }
        return keys;
    }
}

class CharStringEntry {
    char key;
    String value;
    CharStringEntry next;
    public CharStringEntry(char k, String v) { key = k; value = v; }
}

class CharStringMap {
    private CharStringEntry[] table;
    private int capacity = 256;
    public CharStringMap() { table = new CharStringEntry[capacity]; }
    
    public void put(char key, String val) {
        int idx = key % capacity;
        CharStringEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) { curr.value = val; return; }
            curr = curr.next;
        }
        CharStringEntry n = new CharStringEntry(key, val);
        n.next = table[idx];
        table[idx] = n;
    }
    
    public String get(char key) {
        int idx = key % capacity;
        CharStringEntry curr = table[idx];
        while (curr != null) {
            if (curr.key == key) return curr.value;
            curr = curr.next;
        }
        return null;
    }
}

class HuffNode {
    char ch;
    int freq;
    HuffNode left, right;
    public HuffNode(char ch, int freq, HuffNode left, HuffNode right) {
        this.ch = ch; this.freq = freq; this.left = left; this.right = right;
    }
}

// Min-Heap implementado do zero para ordenação de nós
class HuffMinHeap {
    private HuffNode[] heap;
    private int size;
    private int capacity;

    public HuffMinHeap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new HuffNode[capacity];
    }

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    public void add(HuffNode node) {
        if (size == capacity) return; // Evita overflow neste escopo simples
        size++;
        int i = size - 1;
        heap[i] = node;

        while (i != 0 && heap[parent(i)].freq > heap[i].freq) {
            HuffNode temp = heap[i];
            heap[i] = heap[parent(i)];
            heap[parent(i)] = temp;
            i = parent(i);
        }
    }

    public HuffNode poll() {
        if (size <= 0) return null;
        if (size == 1) {
            size--;
            return heap[0];
        }

        HuffNode root = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapify(0);

        return root;
    }

    private void heapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;

        if (l < size && heap[l].freq < heap[smallest].freq) smallest = l;
        if (r < size && heap[r].freq < heap[smallest].freq) smallest = r;

        if (smallest != i) {
            HuffNode temp = heap[i];
            heap[i] = heap[smallest];
            heap[smallest] = temp;
            heapify(smallest);
        }
    }

    public int size() { return size; }
}

public class HuffmanCompressor {
    private CharStringMap codes = new CharStringMap();

    public String compress(String text) {
        CharIntMap freqMap = new CharIntMap();
        for (char c : text.toCharArray()) freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);

        char[] keys = freqMap.getKeys();
        // Capacidade deve ser suficiente para todos os caracteres distintos + os nós intermediários
        HuffMinHeap pq = new HuffMinHeap(keys.length * 3 + 10);
        for (char c : keys) {
            pq.add(new HuffNode(c, freqMap.getOrDefault(c, 0), null, null));
        }

        if (pq.size() == 0) return "";
        while (pq.size() > 1) {
            HuffNode left = pq.poll();
            HuffNode right = pq.poll();
            pq.add(new HuffNode('\0', left.freq + right.freq, left, right));
        }

        HuffNode root = pq.poll();
        generateCodes(root, "");

        StringBuilder compressed = new StringBuilder();
        for (char c : text.toCharArray()) compressed.append(codes.get(c));
        return compressed.toString();
    }

    private void generateCodes(HuffNode node, String code) {
        if (node == null) return;
        // Se for folha, guardamos o código (tratando raiz folha no caso de texto monocaracter)
        if (node.left == null && node.right == null) {
            codes.put(node.ch, code.isEmpty() ? "0" : code);
        }
        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }
}