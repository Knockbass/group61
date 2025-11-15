package nl.saxion.game.yourgamename.entities;

public class StatSystem {
    private int knowledge;
    private int mentalHealth;
    private int money;

    public StatSystem(){
        this.knowledge = 1;
        this.mentalHealth = 100;
        this.money = 0;
    }

    public StatSystem(int knowledge, int mentalHealth, int money){      //this class has 3 maint stats that only Player has
        this.knowledge = knowledge;
        this.mentalHealth = mentalHealth;
        this.money = money;
    }

    public int getKnowledge(){
        return this.knowledge;
    }

    public void setKnowledge(int knowledge){
        this.knowledge = knowledge;
    }

    public int getMentalHealth(){
        return this.mentalHealth;
    }

    public void setMentalHealth(int mentalHealth){
        this.mentalHealth = mentalHealth;
    }

    public int getMoney(){
        return this.money;
    }

    public void setMoney(int money){
        this.money = money;
    }
}
