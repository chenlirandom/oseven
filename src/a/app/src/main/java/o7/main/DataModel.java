package o7.main;

// Main class for one EVE character.
class Character {
    private int id = 0;
    private String name = "";

    int getId() { return this.id; }
    String getName() { return this.name; }

    Character(int id) {
        this.id = id;
    }
}