// priority: 0

// Visit the wiki for more info - https://kubejs.com/

console.info('Hello, World! (Loaded server scripts)')

ServerEvents.recipes(event => {
/**
*  event.recipes.tradingStationTrading(Result Item, Input Ingredients[])
*  .processingTime(Int) [optional]
*  .biome(Biome|BiomeTag) [optional]
*  .exclusiveTo(String) [optional]
*  .exclusiveTo(String[]) [optional]
   **/

    // Basic example
    event.recipes.tradingStationTrading(Item.of('minecraft:emerald', 5),[Item.of("5x minecraft:diamond")]);
    event.recipes.tradingStationTrading(Item.of('minecraft:emerald', 5),[Item.of("5x minecraft:oak_log"),Item.of("10x minecraft:birch_log")]).processingTime(100);
    event.recipes.tradingStationTrading(Item.of('minecraft:emerald', 5),[Item.of("5x minecraft:diamond")]).processingTime(100);
    event.recipes.tradingStationTrading(Item.of('minecraft:andesite',2),[Item.of("2x minecraft:cobblestone")]).processingTime(100);

    //Enchanted book result
    event.recipes.tradingStationTrading(Item.of('minecraft:enchanted_book', '{StoredEnchantments:[{id:"power",lvl:5s}]}').strongNBT(),[Item.of("minecraft:stone")]).processingTime(100)

    // With biome requirement
    event.recipes.tradingStationTrading(Item.of('minecraft:diamond_sword', '{Enchantments:[{id:"power",lvl:5s}]}').strongNBT(),[Item.of("5x minecraft:diamond")]).processingTime(100).biome('#minecraft:is_beach');
    event.recipes.tradingStationTrading(Item.of('minecraft:diamond_sword', '{Enchantments:[{id:"mending",lvl:1s}]}').strongNBT(),[Item.of("5x minecraft:diamond")]).processingTime(100).biome('minecraft:plains');

    //With exclusive to requirement (mechanical)
    event.recipes.tradingStationTrading(Item.of('minecraft:diamond_sword', '{Enchantments:[{id:"looting",lvl:1s}]}').strongNBT(),[Item.of("5x minecraft:diamond")]).processingTime(100).exclusiveTo('mechanical');
    event.recipes.tradingStationTrading(Item.of('minecraft:diamond_sword', '{Enchantments:[{id:"sharpness",lvl:1s}]}').strongNBT(),[Item.of("5x minecraft:diamond")]).processingTime(100).exclusiveTo(['powered','mechanical']);

})