package net.joedoe.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Weapon {
    private String name;
    private int range;
    private int damage;
    private int loadedAmmo;
    private int oneRound;
    private int ammoStock;
    private int apForOneAttack;
    private boolean inFight;
    private Sound sound;

    public Weapon(String name, int range, int damage, int oneRound, int loadedAmmo, int apForOneAttack, boolean inFight, String sound) {
        this.name = name;
        this.range = range;
        this.damage = damage;
        this.oneRound = oneRound;
        this.loadedAmmo = loadedAmmo;
        this.apForOneAttack = apForOneAttack;
        this.inFight = inFight;
        this.sound = Gdx.audio.newSound(Gdx.files.internal(sound));
    }

    public void reload() {
        int diff = oneRound - loadedAmmo;
        if (ammoStock >= diff) {
            loadedAmmo += diff;
            ammoStock -= diff;
        } else {
            loadedAmmo += ammoStock;
            ammoStock = 0;
        }
    }

    public void decreaseLoadedAmmo() {
        loadedAmmo -= 1;
    }

    public int getRandomizedDamage() {
        int rand = (int) (Math.random() * 2) + 1; // 1 or 2
        return damage / rand;
    }

    public void playSound() {
        sound.play();
    }
}
