package nl.saxion.game.yourgamename.systems;

import nl.saxion.game.yourgamename.collision.CollisionManager;
import nl.saxion.game.yourgamename.entities.*;

import java.util.List;

public class CombatSystem {

    public void startAttack(Player player) {
        player.hasHitEnemy = false;
        player.attacking = true;
        updateAttackHitbox(player);
    }

    public void updatePlayerAttack(Player player, float delta) {
        player.attackTimer += delta;
        if (player.attackTimer > player.attackDuration) {
            player.attacking = false;
            player.attackTimer = 0;
        }
    }

    public void updateYapperAttack(List<Yapper> entities, Player player, float delta) {
        for (Yapper yapper : entities) {
            boolean touchingPlayer = CollisionManager.isCollidingWithPlayer(yapper);

            if (touchingPlayer && !yapper.attacking) {
                yapper.attacking = true;
                yapper.attackTimer = 0;
            }

            if (yapper.attacking) {
                yapper.attackTimer += delta;

                if (yapper.attackTimer > yapper.attackCooldown) {

                    if (touchingPlayer) {
                        dealDamage(yapper, player);
                        System.out.println("Player hp decreased: " + player.accessStatSystem().getHP());
                    }
                    yapper.attacking = false;
                    yapper.attackTimer = 0;
                }

            }
        }

    }


    public void updateAttackHitbox(Player player) {
        int rectWidth = 25;
        int rectHeight = 25;

        int centerX = player.getX() + player.getWidth() / 2 - rectWidth / 2;
        int centerY = player.getY() + player.getHeight() / 2 - rectHeight / 2;

        if (player.facing == Direction.UP) {
            player.hitbox.x = centerX;
            player.hitbox.y = player.getY() + player.getHeight();
            player.hitbox.width = rectWidth;
            player.hitbox.height = rectHeight;

        } else if (player.facing == Direction.DOWN) {
            player.hitbox.x = centerX;
            player.hitbox.y = player.getY() - rectHeight;
            player.hitbox.width = rectWidth;
            player.hitbox.height = rectHeight;


        } else if (player.facing == Direction.RIGHT) {

            player.hitbox.x = player.getX() + player.getWidth() + rectWidth;
            player.hitbox.y = centerY;
            player.hitbox.width = rectWidth;
            player.hitbox.height = rectHeight;


        } else { // LEFT
            player.hitbox.x = player.getX() - rectWidth;
            player.hitbox.y = centerY;
            player.hitbox.width = rectWidth;
            player.hitbox.height = rectHeight;

        }
    }

    public void applyPlayerAttack(Player player, List<Yapper> entities) {
        for (Yapper entity : entities) {

            if (CollisionManager.isCollidingWithHitbox(player, entity)) {
                dealDamage(player, entity);
                player.hasHitEnemy = true;
                System.out.println("Yapper hp decreased: " + entity.getHp());

                if (entity.getHp() <= 0) {
                    entity.isDead = true;
                }
            }
        }

    }

    public void dealDamage(CombatEntity damager, CombatEntity receiver) {
        if(receiver instanceof Player){
            ((Player) receiver).accessStatSystem().setHP(((Player) receiver).accessStatSystem().getHP() - damager.getAttackDamage());
        } else {
            receiver.setHp(receiver.getHp() - ((Player) damager).accessStatSystem().getKnowldge()); //knowledge is used as a damage for player
        }
    }
}
