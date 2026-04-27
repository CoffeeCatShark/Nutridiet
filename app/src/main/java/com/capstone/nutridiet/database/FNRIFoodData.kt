package com.capstone.nutridiet.database

data class FNRIFood(
    val name: String,
    val calories: Double,   // kcal per 100g
    val sodium: Double,     // mg per 100g
    val protein: Double,    // g per 100g
    val satFat: Double,     // g per 100g
    val category: String
)

object FNRIDatabase {
    val foods = listOf(

        // ─── FISH AND SEAFOOD ──────────────────────────
        FNRIFood("Bangus (Milkfish), raw", 148.0, 78.0, 20.5, 1.8, "Fish"),
        FNRIFood("Bangus (Milkfish), grilled", 195.0, 85.0, 28.0, 2.4, "Fish"),
        FNRIFood("Tilapia, raw", 96.0, 52.0, 20.1, 0.7, "Fish"),
        FNRIFood("Tilapia, steamed", 105.0, 56.0, 21.0, 0.8, "Fish"),
        FNRIFood("Galunggong (Round Scad), raw", 109.0, 68.0, 21.0, 1.2, "Fish"),
        FNRIFood("Galunggong, fried", 198.0, 280.0, 22.0, 3.1, "Fish"),
        FNRIFood("Sardines, canned in tomato sauce", 127.0, 400.0, 16.0, 2.5, "Fish"),
        FNRIFood("Tulingan (Frigate Tuna), raw", 109.0, 45.0, 23.0, 1.0, "Fish"),
        FNRIFood("Dilis (Dried Anchovies)", 262.0, 2580.0, 47.0, 3.2, "Fish"),
        FNRIFood("Hipon (Shrimp), raw", 85.0, 189.0, 18.0, 0.2, "Seafood"),
        FNRIFood("Pusit (Squid), raw", 79.0, 230.0, 16.0, 0.3, "Seafood"),
        FNRIFood("Talaba (Oyster), raw", 68.0, 211.0, 7.0, 0.5, "Seafood"),

        // ─── MEAT AND POULTRY ──────────────────────────
        FNRIFood("Manok (Chicken), raw", 215.0, 70.0, 18.0, 3.8, "Poultry"),
        FNRIFood("Manok (Chicken), boiled", 195.0, 75.0, 25.0, 3.2, "Poultry"),
        FNRIFood("Manok (Chicken), fried", 298.0, 320.0, 28.0, 5.1, "Poultry"),
        FNRIFood("Baboy (Pork), lean raw", 242.0, 62.0, 17.0, 5.2, "Meat"),
        FNRIFood("Baboy (Pork), boiled", 258.0, 65.0, 20.0, 5.8, "Meat"),
        FNRIFood("Baka (Beef), lean raw", 187.0, 65.0, 20.0, 3.5, "Meat"),
        FNRIFood("Baka (Beef), boiled", 218.0, 68.0, 24.0, 4.1, "Meat"),
        FNRIFood("Itlog (Egg), raw", 155.0, 124.0, 13.0, 3.1, "Egg"),
        FNRIFood("Itlog (Egg), boiled", 155.0, 124.0, 13.0, 3.1, "Egg"),

        // ─── VEGETABLES ────────────────────────────────
        FNRIFood("Kangkong (Water Spinach)", 19.0, 45.0, 2.6, 0.1, "Vegetable"),
        FNRIFood("Ampalaya (Bitter Gourd)", 17.0, 5.0, 1.0, 0.1, "Vegetable"),
        FNRIFood("Malunggay (Moringa) leaves", 92.0, 9.0, 6.7, 0.3, "Vegetable"),
        FNRIFood("Sitaw (String Beans)", 31.0, 6.0, 1.8, 0.1, "Vegetable"),
        FNRIFood("Talong (Eggplant)", 25.0, 2.0, 1.0, 0.1, "Vegetable"),
        FNRIFood("Kamote (Sweet Potato)", 86.0, 55.0, 1.6, 0.1, "Vegetable"),
        FNRIFood("Kamote Tops", 44.0, 8.0, 2.3, 0.1, "Vegetable"),
        FNRIFood("Okra (Ladies Finger)", 33.0, 7.0, 1.9, 0.1, "Vegetable"),
        FNRIFood("Pechay (Bok Choy)", 13.0, 65.0, 1.5, 0.1, "Vegetable"),
        FNRIFood("Patola (Sponge Gourd)", 18.0, 3.0, 0.9, 0.1, "Vegetable"),
        FNRIFood("Upo (Bottle Gourd)", 14.0, 2.0, 0.6, 0.1, "Vegetable"),
        FNRIFood("Gabi (Taro)", 112.0, 11.0, 1.5, 0.1, "Vegetable"),
        FNRIFood("Kalabasa (Squash)", 26.0, 1.0, 1.0, 0.1, "Vegetable"),

        // ─── FRUITS ────────────────────────────────────
        FNRIFood("Saging Lakatan (Banana)", 99.0, 1.0, 1.1, 0.1, "Fruit"),
        FNRIFood("Saging Saba (Cardava Banana)", 115.0, 1.0, 1.2, 0.1, "Fruit"),
        FNRIFood("Papaya, ripe", 43.0, 8.0, 0.5, 0.1, "Fruit"),
        FNRIFood("Mangga (Mango), ripe", 65.0, 2.0, 0.5, 0.1, "Fruit"),
        FNRIFood("Mangga (Mango), green", 40.0, 2.0, 0.4, 0.1, "Fruit"),
        FNRIFood("Dalandan (Orange)", 47.0, 0.0, 0.9, 0.1, "Fruit"),
        FNRIFood("Pakwan (Watermelon)", 30.0, 1.0, 0.6, 0.1, "Fruit"),
        FNRIFood("Pinya (Pineapple)", 50.0, 1.0, 0.5, 0.1, "Fruit"),
        FNRIFood("Suha (Pomelo)", 38.0, 1.0, 0.8, 0.1, "Fruit"),
        FNRIFood("Atis (Sugar Apple)", 94.0, 4.0, 2.1, 0.1, "Fruit"),
        FNRIFood("Guava", 68.0, 2.0, 2.6, 0.1, "Fruit"),

        // ─── RICE AND GRAINS ───────────────────────────
        FNRIFood("Kanin (Steamed White Rice)", 180.0, 1.0, 3.7, 0.1, "Grains"),
        FNRIFood("Brown Rice, cooked", 165.0, 1.0, 3.5, 0.2, "Grains"),
        FNRIFood("Tinapay (Bread), white", 265.0, 491.0, 9.0, 0.6, "Grains"),
        FNRIFood("Pancit Bihon, cooked", 160.0, 210.0, 3.5, 0.3, "Grains"),
        FNRIFood("Lugaw (Rice Porridge)", 65.0, 5.0, 1.5, 0.1, "Grains"),

        // ─── LEGUMES ───────────────────────────────────
        FNRIFood("Monggo (Mung Beans), boiled", 105.0, 4.0, 7.0, 0.1, "Legumes"),
        FNRIFood("Tokwa (Tofu), raw", 76.0, 7.0, 8.1, 0.8, "Legumes"),
        FNRIFood("Tokwa (Tofu), fried", 175.0, 14.0, 11.0, 2.5, "Legumes"),
        FNRIFood("Patani (Lima Beans), boiled", 115.0, 2.0, 7.8, 0.2, "Legumes"),

        // ─── DAIRY ─────────────────────────────────────
        FNRIFood("Gatas (Whole Milk)", 61.0, 43.0, 3.2, 1.9, "Dairy"),
        FNRIFood("Evaporated Milk", 134.0, 136.0, 6.8, 4.1, "Dairy"),

        // ─── FILIPINO DISHES ───────────────────────────
        FNRIFood("Adobong Manok (Chicken Adobo)", 250.0, 850.0, 28.0, 4.2, "Filipino Dish"),
        FNRIFood("Sinigang na Baboy", 180.0, 620.0, 22.0, 3.1, "Filipino Dish"),
        FNRIFood("Sinigang na Bangus", 165.0, 580.0, 24.0, 2.1, "Filipino Dish"),
        FNRIFood("Tinolang Manok", 165.0, 540.0, 25.0, 2.1, "Filipino Dish"),
        FNRIFood("Pinakbet", 95.0, 480.0, 4.0, 0.8, "Filipino Dish"),
        FNRIFood("Monggo Guisado", 142.0, 380.0, 9.0, 0.5, "Filipino Dish"),
        FNRIFood("Kare-Kare", 310.0, 420.0, 30.0, 5.8, "Filipino Dish"),
        FNRIFood("Nilaga", 195.0, 520.0, 22.0, 3.8, "Filipino Dish"),
        FNRIFood("Lechon Kawali", 420.0, 680.0, 24.0, 12.4, "Filipino Dish"),
        FNRIFood("Paksiw na Bangus", 175.0, 620.0, 22.0, 2.8, "Filipino Dish"),
        FNRIFood("Ginisang Ampalaya", 65.0, 320.0, 5.0, 1.2, "Filipino Dish"),
        FNRIFood("Laing (Taro in Coconut Milk)", 245.0, 320.0, 5.0, 8.5, "Filipino Dish"),
        FNRIFood("Bulalo", 385.0, 480.0, 28.0, 9.2, "Filipino Dish"),
        FNRIFood("Arroz Caldo", 145.0, 380.0, 8.0, 1.5, "Filipino Dish")
    )

    // search FNRI foods by name
    fun search(query: String): List<FNRIFood> {
        if (query.length < 2) return emptyList()
        return foods.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }.take(5)  // return top 5 matches
    }
}
