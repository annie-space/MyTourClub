package com.example.mytourclub.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytourclub.R
import com.example.mytourclub.adapter.RouteAdapter
import com.example.mytourclub.data.AppDatabase
import com.example.mytourclub.model.Route
import com.example.mytourclub.util.UserManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.util.Log
import androidx.appcompat.widget.SearchView
import android.widget.ImageView
import com.example.mytourclub.model.Equipment
import com.example.mytourclub.model.RouteEquipment
import com.example.mytourclub.until.Difficulty
import com.example.mytourclub.until.Direction
import com.example.mytourclub.until.Typing
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), RouteAdapter.OnItemClickListener {

    private lateinit var routeAdapter: RouteAdapter
    private lateinit var searchView: SearchView
    private var selectedItemId: Int = R.id.navigation_home
    private var routesList: List<Route> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Получение данных из UserManager
        val userId = UserManager.userId
        val userRole = UserManager.userRole
        selectedItemId = intent.getIntExtra("selectedItemId", R.id.navigation_home)

        if (userId == -1 || userRole.isNullOrEmpty()) {
            Log.e("MainActivity", "Не удалось получить userId или userRole из UserManager")
            finish()
            return
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRoutes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterRoutes(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterRoutes(it) }
                return false
            }
        })

        val ivLogout = findViewById<ImageView>(R.id.ivLogout)
        ivLogout.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = selectedItemId
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Остаемся на текущей активности
                    true
                }
                R.id.navigation_user -> {
                    when (userRole) {
                        "user" -> {
                            val intent = Intent(this, UserActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }
                        "admin" -> {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }
                        "guide" -> {
                            val intent = Intent(this, GuideActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                            finish()
                        }
                    }
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("selectedItemId", R.id.navigation_profile)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@MainActivity)
            //clearTables(db)
            //populateTables(db)

            routesList = db.routeDao().getAllRoutes()
            Log.d("MainActivity", "Loaded routes from DB: $routesList")

            routeAdapter = RouteAdapter(routesList, this@MainActivity)
            recyclerView.adapter = routeAdapter
        }
    }

    private fun filterRoutes(query: String) {
        val filteredRoutes = routesList.filter {
            it.name.contains(query, ignoreCase = true)
        }
        routeAdapter.updateList(filteredRoutes)
    }

    override fun onItemClick(route: Route) {
        val intent = Intent(this, RouteDetailActivity::class.java)
        intent.putExtra("routeId", route.id)
        startActivity(intent)
    }

    private suspend fun clearTables(db: AppDatabase) {
        db.bookingDao().deleteAllBookings()
        db.rentDao().deleteAllRents()
        db.equipmentDao().deleteAllEquipment()
        db.routeEquipmentDao().deleteAllRouteEquipment()
        db.routeDao().deleteAllRoutes()
        Log.d("MainActivity", "All tables cleared")
    }
    private suspend fun populateTables(db: AppDatabase) {
        // Заполнение таблицы Equipment
       val equipmentList = listOf(
            Equipment(name = "Палатка", quantity = 10, description = "Прочная палатка", photo = R.drawable.icon, priceUnit = 2500.0),
            Equipment(name = "Спальный мешок", quantity = 20, description = "Теплый спальный мешок", photo = R.drawable.icon, priceUnit = 750.0),
            Equipment(name = "Рюкзак", quantity = 15, description = "Удобный рюкзак", photo = R.drawable.icon, priceUnit = 830.0),
            Equipment(name = "Термос", quantity = 30, description = "Вакуумный термос для горячих напитков", photo = R.drawable.icon, priceUnit = 100.0),
            Equipment(name = "Горелка", quantity = 40, description = "Газовая горелка для приготовления пищи на открытом воздухе", photo = R.drawable.icon, priceUnit = 90.0),
            Equipment(name = "Туристический коврик", quantity = 45, description = "Удобный коврик для сна на открытом воздухе", photo = R.drawable.icon, priceUnit = 80.0),
            Equipment(name = "Фонарик", quantity = 45, description = "Яркий светодиодный фонарик с длительным временем работы", photo = R.drawable.icon, priceUnit = 50.0),
            Equipment(name = "Компас", quantity = 30, description = "Надежный компас для навигации", photo = R.drawable.icon, priceUnit = 120.0),
            Equipment(name = "Термобелье", quantity = 20, description = "Комплект термобелья для холодной погоды", photo = R.drawable.icon, priceUnit = 950.0),
            Equipment(name = "Треккинговые палки", quantity = 25, description = "Регулируемые треккинговые палки для поддержки", photo = R.drawable.icon, priceUnit = 330.0)

        )
        db.equipmentDao().insertAll(equipmentList)
        Log.d("MainActivity", "Added equipment: $equipmentList")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        val routeList = listOf(
            Route(
                name = "Путешествие по Карелии",
                direction = Direction.KARELIA,
                difficulty = Difficulty.TWO,
                duration = 8,
                dates = dateFormat.parse("2025-06-10"),
                length = 120,
                photoResId = R.drawable.karelia,
                description = "Путешествие по живописным озёрам и густым лесам Карелии подарит вам уникальную возможность насладиться природной красотой северного края России. Этот маршрут идеально подходит для любителей активного отдыха, рыбалки и каякинга. Вы сможете увидеть уникальные пейзажи и узнать много интересного о культуре и истории Карелии.",
                places = 20,
                available_places = 15,
                type = Typing.WATER,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 18000.0
            ),
            Route(
                name = "Байкал",
                direction = Direction.NORTH,
                difficulty = Difficulty.TWO,
                duration = 8,
                dates = dateFormat.parse("2025-06-10"),
                length = 120,
                photoResId = R.drawable.bikal,
                description = "Путешествие вокруг величественного озера Байкал - самого глубокого озера в мире. Вы сможете насладиться кристально чистой водой, уникальной флорой и фауной, а также посетить небольшие деревни и встретить местных жителей, которые расскажут вам о своих традициях и быте. Незабываемые закаты и рассветы над Байкалом останутся в вашей памяти навсегда.",
                places = 20,
                available_places = 15,
                type = Typing.KAYAK,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 18000.0
            ),
            Route(
                name = "Исследование Камчатки",
                direction = Direction.EAST,
                difficulty = Difficulty.FOUR,
                duration = 12,
                dates = dateFormat.parse("2025-07-15"),
                length = 150,
                photoResId = R.drawable.kamchatka,
                description = "Исследуйте вулканы и гейзеры Камчатки - одного из самых диких и не тронутых уголков России. Этот маршрут включает в себя походы к активным вулканам, купание в горячих источниках и возможность увидеть уникальных животных, таких как бурые медведи и орланы. Камчатка впечатлит вас своими природными чудесами и захватывающими видами.",
                places = 10,
                available_places = 5,
                type = Typing.EXPEDITION,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 22000.0
            ),
            Route(
                name = "Горы Урала",
                direction = Direction.URAL,
                difficulty = Difficulty.THREE,
                duration = 7,
                dates = dateFormat.parse("2025-08-20"),
                length = 90,
                photoResId = R.drawable.ural,
                description = "Путешествие по горам и лесам Урала предлагает уникальные возможности для трекинга и альпинизма. Этот маршрут проведет вас через древние горные массивы, кристальные реки и густые леса. Вы сможете побывать на многочисленных обзорных точках, насладиться видами и провести незабываемые ночи у костра под звездами.",
                places = 25,
                available_places = 20,
                type = Typing.TREKKING,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 12000.0
            ),
            Route(
                name = "Кавказские высоты",
                direction = Direction.KAVKAZ,
                difficulty = Difficulty.FIVE,
                duration = 14,
                dates = dateFormat.parse("2025-09-10"),
                length = 200,
                photoResId = R.drawable.caucasus,
                description = "Исследуйте величественные Кавказские горы, восхождение на которых потребует от вас силы и выносливости. Этот маршрут включает в себя походы к ледникам и водопадам, посещение древних горных поселений и знакомство с культурой местных народов. Кавказ впечатлит вас своими великолепными пейзажами и приключенческими возможностями.",
                places = 12,
                available_places = 8,
                type = Typing.MOUNTAIN,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 25000.0
            ),
            Route(
                name = "Красноярские Столбы",
                direction = Direction.SIBERIA,
                difficulty = Difficulty.THREE,
                duration = 9,
                dates = dateFormat.parse("2025-10-01"),
                length = 80,
                photoResId = R.drawable.krasnoyarsk,
                description = "Путешествие по Красноярским Столбам предлагает уникальные скалолазные и трекинговые маршруты. Этот природный парк известен своими гигантскими скальными образованиями, которые привлекают альпинистов и туристов со всего мира. Вы сможете исследовать многочисленные тропы и насладиться видами на Красноярский край.",
                places = 30,
                available_places = 25,
                type = Typing.MOUNTAIN,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 16000.0
            ),
            Route(
                name = "Алтайское приключение",
                direction = Direction.ALTAI,
                difficulty = Difficulty.THREE,
                duration = 10,
                dates = dateFormat.parse("2025-05-15"),
                length = 70,
                photoResId = R.drawable.altai,
                description = "Путешествие по Алтайским горам предложит вам захватывающие виды и необычные природные явления. Этот маршрут проведет вас через высокогорные долины, кристальные озера и древние петроглифы. Вы сможете посетить уникальные культурные памятники и познакомиться с жизнью местных жителей.",
                places = 15,
                available_places = 10,
                type = Typing.MOUNTAIN,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 15000.0
            ),
            Route(
                name = "Кольский полуостров",
                direction = Direction.NORTH,
                difficulty = Difficulty.TWO,
                duration = 11,
                dates = dateFormat.parse("2025-06-01"),
                length = 110,
                photoResId = R.drawable.kola,
                description = "Путешествие по северной части России, на Кольском полуострове, предложит вам уникальные возможности для трекинга и наблюдения за северным сиянием. Этот маршрут включает в себя посещение тундры, лесов и гор, а также знакомство с культурой саамов. Впечатляющие пейзажи и дикая природа останутся в вашей памяти надолго.",
                places = 20,
                available_places = 15,
                type = Typing.TREKKING,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 18000.0
            ),
            Route(
                name = "Саянские горы",
                direction = Direction.SIBERIA,
                difficulty = Difficulty.FOUR,
                duration = 12,
                dates = dateFormat.parse("2025-07-05"),
                length = 130,
                photoResId = R.drawable.sayans,
                description = "Путешествие по Саянским горам предлагает захватывающие горные маршруты и уникальные природные пейзажи. Этот маршрут проведет вас через живописные долины и леса, кристальные озера и горячие источники. Вы сможете увидеть редких животных и насладиться уединением в дикой природе.",
                places = 15,
                available_places = 10,
                type = Typing.EXPEDITION,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 20000.0
            ),
            Route(
                name = "Хибины",
                direction = Direction.NORTH,
                difficulty = Difficulty.TWO,
                duration = 8,
                dates = dateFormat.parse("2025-08-01"),
                length = 95,
                photoResId = R.drawable.khibiny,
                description = "Путешествие по Саянским горам предлагает захватывающие горные маршруты и уникальные природные пейзажи. Этот маршрут проведет вас через живописные долины и леса, кристальные озера и горячие источники. Вы сможете увидеть редких животных и насладиться уединением в дикой природе.",
                places = 18,
                available_places = 12,
                type = Typing.TREKKING,
                admin_id = 1,
                guide1_id = 1,
                guide2_id = 2,
                price = 17000.0
            )

        )


        db.routeDao().insertAll(routeList)
        Log.d("MainActivity", "Added routes: $routeList")

        // Заполнение таблицы RouteEquipment для маршрута с id 11
        val routeEquipmentList = listOf(
            RouteEquipment(routeId = 1, equipmentId = 1, quantityRequired = 2),
            RouteEquipment(routeId = 1, equipmentId = 2, quantityRequired = 3),
            RouteEquipment(routeId = 1, equipmentId = 3, quantityRequired = 1)
        )
        db.routeEquipmentDao().insertAll(routeEquipmentList)
        Log.d("MainActivity", "Added route equipment: $routeEquipmentList")
    }
}
