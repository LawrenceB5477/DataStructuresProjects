import java.util.ArrayList;
import java.util.List;

public class CuckooHashMap<K, V> {
    private Entry<K, V>[] array1;
    private Entry<K, V>[] array2;
    private int size;
    private int capacity;
    private int prime;

    public CuckooHashMap() {
        this(4);
    }

    public CuckooHashMap(int cap) {
        this(cap, 10007);
    }

    public CuckooHashMap(int cap, int prime) {
        if (cap % 2 != 0) {
            cap += 1;
        }
        this.array1 = new Entry[cap / 2];
        this.array2 = new Entry[cap / 2];
        this.capacity = cap;
        this.prime = prime;
    }

    public float loadFactor() {
        return ((float) this.size) / this.capacity;
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.capacity;
    }

    public V get(K key) {
        if (array1[h1(key)] != null && array1[h1(key)].getKey().equals(key)) {
            return array1[h1(key)].getValue();
        } else if (array2[h2(key)] != null && array2[h2(key)].getKey().equals(key)) {
            return array2[h2(key)].getValue();
        } else {
            return null;
        }
    }

    public V remove(K key) {
        V value = null;
        if (array1[h1(key)] != null && array1[h1(key)].getKey().equals(key)) {
            value = array1[h1(key)].getValue();
            array1[h1(key)] = null;
            size--;
        } else if (array2[h2(key)] != null && array2[h2(key)].getKey().equals(key)) {
            value = array2[h2(key)].getValue();
            array2[h2(key)] = null;
            size--;
        }

        if (loadFactor() < .25) {
            if (capacity() / 2 >= 4) {
                resize(capacity() / 2);
            }
        }
        return value;
    }

    public V put(K key, V value) {
        int collisions = 0;
        boolean found = false;
        boolean usingh1 = true;
        V returnTemp = null;

        if (loadFactor() > .5) {
            resize(capacity() * 2);
            printMap();
        }

        while (!found) {
            if (collisions >= capacity()) {
                rehash();
                collisions = 0;
            }

            //Test if key is already in map
            Entry<K, V> entryOne = array1[h1(key)];
            Entry<K, V> entryTwo = array2[h2(key)];

            if (entryOne != null && key.equals(entryOne.getKey())) {
                returnTemp = entryOne.getValue();
                array1[h1(key)].setValue(value);
                break;
            }

            if (entryTwo != null && key.equals(entryTwo.getKey())) {
                returnTemp = entryTwo.getValue();
                array2[h2(key)].setValue(value);
                break;
            }

            //looking in the first array
            if (usingh1) {
                //Collision
                if (entryOne != null) {
                    Entry<K, V> temp = entryOne;
                    array1[h1(key)] = new Entry<>(key, value);
                    key = temp.getKey();
                    value = temp.getValue();
                    usingh1 = false;
                    collisions++;

                } else {
                    array1[h1(key)] = new Entry<>(key, value);
                    found = true;
                }

                //looking in the second array
            } else {
                if (entryTwo != null) {
                    Entry<K, V> temp = entryTwo;
                    array2[h2(key)] = new Entry<>(key, value);
                    key = temp.getKey();
                    value = temp.getValue();
                    usingh1 = true;
                    collisions++;

                } else {
                    array2[h2(key)] = new Entry<>(key, value);
                    found = true;
                }
            }
        }

        //If we added a distinct value
        if (returnTemp == null) {
            size++;

        }
        return returnTemp;
    }

    public void printMap() {
        System.out.println();
        System.out.println("size now: " + size());
        System.out.println(prime);
        System.out.println("--------------------------------------------------------------");
        for (Entry<K, V> entry : array1) {
            if (entry == null) {
                System.out.print(" null ");
            } else {
                System.out.print(" (" + entry.getKey() + " - " + entry.getValue() + ") ");
            }
        }
        System.out.println();
        System.out.println("--------------------------------------------------------------");
        for (Entry<K, V> entry : array2) {
            if (entry == null) {
                System.out.print(" null ");
            } else {
                System.out.print(" (" + entry.getKey() + " - " + entry.getValue() + ") ");
            }
        }
        System.out.println();
        System.out.println("--------------------------------------------------------------");
    }

    private void reinsert() {
        Entry<K, V>[] old1 = this.array1;
        Entry<K, V>[] old2 = this.array2;


        this.array1 = new Entry[this.capacity / 2];
        this.array2 = new Entry[this.capacity / 2];
        this.size = 0;

        for (int i = 0; i < old1.length; i++) {
            if (old1[i] != null) {
                put(old1[i].getKey(), old1[i].getValue());
            }
        }

        for (int i = 0; i < old2.length; i++) {
            if (old2[i] != null) {
                put(old2[i].getKey(), old2[i].getValue());
            }
        }
    }

    private void resize(int newCap) {
        this.capacity = newCap % 2 != 0 ? newCap + 1 : newCap;
        reinsert();
    }

    private void rehash() {
        boolean prime = true;
        int nextPrime = this.prime + 1;
        while (true) {
            prime = true;
            for (int i = 2; i <= Math.floor(Math.sqrt(nextPrime)) + 1; i++) {
                if (nextPrime % i == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime) {
                this.prime = nextPrime;
                break;
            }
            nextPrime++;
        }

        reinsert();
    }

    public int h1(K key) {
        return (Math.abs(key.hashCode()) % this.prime) % (this.capacity() / 2);
    }

    public int h2(K key) {
        return ((Math.abs(key.hashCode()) / this.prime) % this.prime) % (this.capacity() / 2);
    }

    public Iterable<Entry<K, V>> entrySet() {
        List<Entry<K, V>> entries = new ArrayList<>();

        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != null) {
                entries.add(array1[i]);
            }

            if (array2[i] != null) {
                entries.add(array2[i]);
            }
        }

        return entries;
    }

    public Iterable<K> keySet() {
        List<Entry<K, V>> entries = (ArrayList) entrySet();
        List<K> keys = new ArrayList<>();
        for (Entry<K, V> entry : entries) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    public Iterable<V> valueSet() {
        List<Entry<K, V>> entries = (ArrayList) entrySet();
        List<V> values = new ArrayList<>();
        for (Entry<K, V> entry : entries) {
            values.add(entry.getValue());
        }
        return values;
    }
}
