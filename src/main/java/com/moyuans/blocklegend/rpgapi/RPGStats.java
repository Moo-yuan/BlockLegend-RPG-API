package com.moyuans.blocklegend.rpgapi;

public class RPGStats {

    private float attackDamage;
    private float critChance;
    private float critDamage;
    private float baseDefense;
    private float currentHealth;
    private float maxHealth;
    private float skillDamage;
    private float skillCritChance;
    private float skillCritDamage;
    private float parryChance;
    private float blockChance;
    private float dodgeChance;
    private String rawText;
    private boolean valid;

    public float getAttackDamage() { return attackDamage; }
    public float getCritChance() { return critChance; }
    public float getCritDamage() { return critDamage; }
    public float getBaseDefense() { return baseDefense; }
    public float getCurrentHealth() { return currentHealth; }
    public float getMaxHealth() { return maxHealth; }
    public float getSkillDamage() { return skillDamage; }
    public float getSkillCritChance() { return skillCritChance; }
    public float getSkillCritDamage() { return skillCritDamage; }
    public float getParryChance() { return parryChance; }
    public float getBlockChance() { return blockChance; }
    public float getDodgeChance() { return dodgeChance; }
    public String getRawText() { return rawText; }
    public boolean isValid() { return valid; }

    void setAttackDamage(float value) { this.attackDamage = value; }
    void setCritChance(float value) { this.critChance = value; }
    void setCritDamage(float value) { this.critDamage = value; }
    void setBaseDefense(float value) { this.baseDefense = value; }
    void setCurrentHealth(float value) { this.currentHealth = value; }
    void setMaxHealth(float value) { this.maxHealth = value; }
    void setSkillDamage(float value) { this.skillDamage = value; }
    void setSkillCritChance(float value) { this.skillCritChance = value; }
    void setSkillCritDamage(float value) { this.skillCritDamage = value; }
    void setParryChance(float value) { this.parryChance = value; }
    void setBlockChance(float value) { this.blockChance = value; }
    void setDodgeChance(float value) { this.dodgeChance = value; }
    void setRawText(String text) { this.rawText = text; }
    void setValid(boolean valid) { this.valid = valid; }

    @Override
    public String toString() {
        return String.format(
            "RPGStats[attack=%.1f, crit=%.1f, critDmg=%.1f, def=%.1f, hp=%.1f/%.1f, skillDmg=%.1f, skillCrit=%.1f, skillCritDmg=%.1f, parry=%.1f, block=%.1f, dodge=%.1f]",
            attackDamage, critChance, critDamage, baseDefense, currentHealth, maxHealth,
            skillDamage, skillCritChance, skillCritDamage, parryChance, blockChance, dodgeChance
        );
    }
}
