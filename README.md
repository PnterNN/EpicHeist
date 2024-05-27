# Minecraft Heist Event Plugin

This plugin adds a heist event to Minecraft servers. Players go to a bank at the end of a timer to steal gold blocks. Meanwhile, the police try to catch the players. Players must escape from the bank without getting caught and avoid being sent to jail.

## Features

- **Timer:** The event runs for a specified duration. At the end of the timer, players try to escape from the bank while police attempt to catch them.
- **Bank Explosion:** When the timer ends, the bank doors explode, and players start stealing gold blocks.
- **Team Visibility:** Players can only see their teammates.
- **Police:** Police arrive when the timer ends and try to catch the players.
- **Jail:** Players caught by the police or those who fall from the parkour are sent to jail, and half of their stolen gold is confiscated.
- **Winning Teams:** At the end of the event, the top 3 teams are announced in the chat and receive rewards.

# Config
```
prefix: '&3Heist &8» '
main-server: true #IF REDIS FALSE MUST BE TRUE
sql:
  enabled: false
  host: '127.0.0.1'
  port: 3306
  database: 'epicheist'
  username: ''
  password: ''
redis:
  enabled: false
  host: '127.0.0.1'
  port: 6379
  channel: 'epicheist'
  password: ''
locations:
  swat-location:
    x: 0.0
    y: 0.0
    z: 0.0
    world: world
  bank-location:
    x: 0.0
    y: 0.0
    z: 0.0
    world: world
  jail-location:
    x: 0.0
    y: 0.0
    z: 0.0
    world: world
regions:
  world-name: world
  vault-name: vault
  entrance-door-name: heist_entrance_door
  exit-door-name: heist_exit_door
  teleport-region: heist_teleport_region
timer:
  waiting-state:
    specific:
      days: 0
      hours: 0
      minutes: 0
      seconds: 30
    time: '12:30:30'
    week-day: 1 # 1 = Sunday, 2 = Monday, 3 = Tuesday, 4 = Wednesday, 5 = Thursday, 6 = Friday, 7 = Saturday
    month-day: 15
    loop: EVERYDAY # EVERYDAY, EVERYWEEK, EVERYMONTH, SPECIFIC
  starting-state:
    days: 0
    hours: 0
    minutes: 0
    seconds: 10
  playing-state:
    days: 0
    hours: 0
    minutes: 0
    seconds: 50
  swat-state:
    days: 0
    hours: 0
    minutes: 0
    seconds: 10
  escaping-state:
    days: 0
    hours: 0
    minutes: 0
    seconds: 30
messages:
  not-enough-players: '&cNot enough players to start the heist!'
  gold-stealing-player: '&7you has stolen &e{gold} &7gold and &e{money} &7money from
    the vault!'
  most-gold-stealing-announcement:
    - '&6Top 3 Crews:'
    - ''
    - '&7&l {crew_1} &e{gold_1} Gold, {money_1}$ &7({percent_1} Gold/per player)'
    - '&7&l {crew_2} &e{gold_2} Gold, {money_2}$ &7({percent_2} Gold/per player)'
    - '&7&l {crew_3} &e{gold_3} Gold, {money_3}$ &7({percent_3} Gold/per player)'
    - ''
time-formats:
  day: '{day} Gün {hour} Saat {minute} dk {second}sn'
  hour: '{hour} Saat {minute} dk {second}sn'
  minute: '{minute} dk {second}sn'
  second: '{second}sn'
musics:
  starting-state: epicheist:music.starting_state
  playing-state: epicheist:music.playing_state
  swat-state: epicheist:swat.coming
  escaping-state: epicheist:music.escaping_state
  ending-state: epicheist:music.ending_state
  gold-steal: epicheist:gold.steal
  gold-steal-big: epicheist:gold.steal_big
  catch: epicheist:swat.elemination
  crew-1: epicheist:rewards.crew1
  crew-2: epicheist:rewards.crew2
  crew-3: epicheist:rewards.crew3
bossbar:
  title: '{OWN_CREW_PERCENT}% Gold / {FIRST_CREW_PERCENT}% Gold'
  color: YELLOW
animated-titles:
  heist-cancel:
    title: HEIST CANCELLED
    subtitle: The heist has been cancelled!
    background-color: '&7'
    title-color: '&c'
  catch-title:
    title: YOU CAUGHT
    subtitle: Half the money was taken!
    background-color: '&7'
    title-color: '&c'
  starting-state:
    title: BANK OPENED
    subtitle: 30 seconds until the gate explodes!
    background-color: '&7'
    title-color: '&e'
  playing-state:
    title: GATE EXPLODED
    subtitle: Rob the bank and escape!
    background-color: '&7'
    title-color: '&e'
  swat-state:
    title: SWAT ARRIVED
    subtitle: run away from the SWAT team!
    background-color: '&7'
    title-color: '&e'
  escaping-state:
    title: ESCAPE
    subtitle: Run to the exploded gate!
    background-color: '&7'
    title-color: '&e'
  successfully-escaped:
    title: ESCAPE SUCCESS
    subtitle: You have successfully escaped!
    background-color: '&7'
    title-color: '&e'
  ending-state:
    title: HEIST ENDED
    subtitle: The heist has ended!
    background-color: '&7'
    title-color: '&e'
gold:
  big-gold:
    block: RAW_GOLD_BLOCK
    chance: 1
    sound: epicheist:gold.steal_big
    money:
      default:
        permission: epicheist.default
        min: 100
        max: 200
      vip:
        permission: epicheist.vip
        min: 200
        max: 400
  small-gold:
    block: GOLD_BLOCK
    sound: epicheist:gold.steal
    money:
      default:
        permission: epicheist.default
        min: 10
        max: 20
      vip:
        permission: epicheist.vip
        min: 20
        max: 40
swat:
  name: '&1SWAT'
  amount: 2 # per player
  speed-level: 1
  jump: 0.5 # jump height (jump per 3 second)
  armor:
    helmet: DIAMOND_HELMET
    chestplate: DIAMOND_CHESTPLATE
    leggings: DIAMOND_LEGGINGS
    boots: DIAMOND_BOOTS
rewards:
  1: []
  2: []
  3: []
```
# Menus
```
admin-panel-gui:
  title: '&0Soygun Admin Paneli'
  row: 4
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    locations:
      display-name: '&6&lLokasyonlar'
      material: COMPASS
      slot: 20
      lore:
        - '&r'
        - '&7Belirlemeniz gereken lokasyonlar'
        - '&7bu menüde yer alır.'
        - '&r'
        - '&e► &f/heist locations'
        - '&r'
        - '&aTıkla ve aç!'
    rewards:
      display-name: '&6&lÖdüller'
      material: CAULDRON
      slot: 22
      lore:
        - '&r'
        - '&7Soygun etkinliği için'
        - '&7belirli ödülleri ayarlar'
        - '&r'
        - '&e► &f/heist rewards'
        - '&r'
        - '&aTıkla ve aç!'
admin-panel-gui-locations:
  title: '&0Soygun &8> Lokasyonlar'
  row: 4
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    back-menu:
      display-name: '&cGeri dön'
      material: ARROW
      slot: 0
      lore:
        - '&7Ana menüye dönün.'
    bank-spawn-location:
      display-name: '&6&nBanka başlangıç lokasyonu'
      material: GOLD_BLOCK
      slot: 20
      lore:
        - '&r'
        - '&7Oyuncular /banka yazdıklarında,'
        - '&7ışınlanacakları lokasyon.'
        - '&r'
        - '&e► &f/heist setBankSpawn'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ışınlanın!'
    jail-spawn-location:
      display-name: '&6&nHapis başlangıç lokasyonu'
      material: IRON_BARS
      slot: 22
      lore:
        - '&r'
        - '&7Oyuncular özel time yakalandığında,'
        - '&7ışınlanacakları lokasyon.'
        - '&r'
        - '&e► &f/heist setJailSpawn'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ışınlanın!'
    swat-spawn-location:
      display-name: '&6&nÖzel tim başlangıç lokasyonu'
      material: ZOMBIE_HEAD
      slot: 24
      lore:
        - '&r'
        - '&7Özel timin doğacağı lokasyon'
        - '&r'
        - '&e► &f/heist setSwatSpawn'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ışınlanın!'
admin-panel-gui-rewards:
  title: '&0Soygun &8> Ödüller'
  row: 4
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    back-menu:
      display-name: '&cGeri dön'
      material: ARROW
      slot: 0
      lore:
        - '&7Ana menüye dönün.'
    first-crew:
      display-name: '&6&n1. Soyguncu takımı'
      material: TORCH
      slot: 20
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&71. takım ödüllerini ayarlayın'
        - '&r'
        - '&e► &f/heist setreward 1'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ödüllere bakın!'
    second-crew:
      display-name: '&6&n2. Soyguncu takımı'
      material: REDSTONE_TORCH
      slot: 22
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&72. takım ödüllerini ayarlayın'
        - '&r'
        - '&e► &f/heist setreward 2'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ödüllere bakın!'
    third-crew:
      display-name: '&6&n3. Soyguncu takımı'
      material: SOUL_TORCH
      slot: 24
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&73. takım ödüllerini ayarlayın'
        - '&r'
        - '&e► &f/heist setreward 3'
        - '&r'
        - '&aSol-Tık ile ayarlayın'
        - '&aSağ-Tık ile ödüllere bakın!'
admin-panel-gui-rewards-first:
  title: '&0Soygun &8> Ödüller > 1'
  row: 6
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    back-menu:
      display-name: '&cGeri dön'
      material: ARROW
      slot: 0
      lore:
        - '&7Ödüller menüsüne dönün.'
    save-item:
      display-name: '&a&lKaydet'
      material: EMERALD_BLOCK
      slot: 4
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&71. takımın ödüllerini ayarlayın.'
        - '&r'
        - '&aSol-Tık ile kaydet'
admin-panel-gui-rewards-second:
  title: '&0Soygun &8> Ödüller > 2'
  row: 6
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    back-menu:
      display-name: '&cGeri dön'
      material: ARROW
      slot: 0
      lore:
        - '&7Ödüller menüsüne dönün.'
    save-item:
      display-name: '&a&lKaydet'
      material: EMERALD_BLOCK
      slot: 4
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&72. takımın ödüllerini ayarlayın.'
        - '&r'
        - '&aSol-Tık ile kaydet'
admin-panel-gui-rewards-third:
  title: '&0Soygun &8> Ödüller > 3'
  row: 6
  icons:
    filler-item:
      display-name: ' '
      material: BLACK_STAINED_GLASS_PANE
      slot: 0-8
    back-menu:
      display-name: '&cGeri dön'
      material: ARROW
      slot: 0
      lore:
        - '&7Ödüller menüsüne dönün.'
    save-item:
      display-name: '&a&lKaydet'
      material: EMERALD_BLOCK
      slot: 4
      lore:
        - '&r'
        - '&7En çok altın toplayan,'
        - '&73. takımın ödüllerini ayarlayın.'
        - '&r'
        - '&aSol-Tık ile kaydet'```
