package com.example.app_tv.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.app_tv.data.models.Admin
import com.example.app_tv.data.models.BookBorrowStat
import com.example.app_tv.data.models.Book
import com.example.app_tv.data.models.BookOrder
import com.example.app_tv.data.models.BorrowSlip
import com.example.app_tv.data.models.Category
import com.example.app_tv.data.models.Member
import com.example.app_tv.data.models.Supplier1

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "library.db"

        private const val TABLE_ADMIN = "Admin"
        private const val TABLE_MEMBER = "Member"
        private const val TABLE_CATEGORY = "Category"
        private const val TABLE_BOOK = "Book"
        private const val TABLE_BORROW_SLIP = "BorrowSlip"
        private const val TABLE_SUPPLIER = "Supplier"
        private const val TABLE_BOOKORDER = "BookOrder"


        private const val SQL_CREATE_ADMIN = """
            CREATE TABLE $TABLE_ADMIN (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT,
                username TEXT UNIQUE,
                password TEXT
            )
        """

        private const val SQL_CREATE_MEMBER = """
            CREATE TABLE $TABLE_MEMBER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                email TEXT UNIQUE,
                imageUri TEXT
            )
        """


        private const val SQL_CREATE_BOOK = """
            CREATE TABLE $TABLE_BOOK (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                imageUri TEXT,
                category_Id INTEGER,
                quantity INTEGER,  
                price REAL,

                FOREIGN KEY (category_Id) REFERENCES $TABLE_CATEGORY (id)
            )
        """


        private const val SQL_CREATE_CATEGORY = """
            CREATE TABLE $TABLE_CATEGORY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE
            )
        """
        private const val SQL_CREATE_SUPPLIER = """
            CREATE TABLE $TABLE_SUPPLIER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT
            )
        """
        private const val SQL_CREATE_BORROW_SLIP = """
            CREATE TABLE $TABLE_BORROW_SLIP (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                memberId INTEGER,
                bookId INTEGER,
                borrowDate TEXT,
                returnDate TEXT,
                status TEXT,
                FOREIGN KEY (memberId) REFERENCES $TABLE_MEMBER (id),
                FOREIGN KEY (bookId) REFERENCES $TABLE_BOOK (id)
            )
        """
        private const val SQL_CREATE_BOOKORDER = """
            CREATE TABLE $TABLE_BOOKORDER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bookId INTEGER,
                supplierId INTEGER,
                quantityOrder INTEGER,
                orderDate TEXT,
                isStocked BOOLEAN,
                FOREIGN KEY (bookId) REFERENCES $TABLE_BOOK (id),
                FOREIGN KEY (supplierId) REFERENCES $TABLE_SUPPLIER (id)
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ADMIN)
        db.execSQL(SQL_CREATE_MEMBER)
        db.execSQL(SQL_CREATE_CATEGORY)
        db.execSQL(SQL_CREATE_BOOK)
        db.execSQL(SQL_CREATE_BORROW_SLIP)
        db.execSQL(SQL_CREATE_SUPPLIER)
        db.execSQL(SQL_CREATE_BOOKORDER)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADMIN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOK")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BORROW_SLIP")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SUPPLIER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKORDER")

        onCreate(db)
    }

    // Thao tác với bảng Admin
    fun addAdmin(username: String, password: String, fullName: String, email: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val values = ContentValues().apply {
                put("username", username)
                put("password", password)
                put("name", fullName)
                put("email", email)
            }
            val adminId = db.insert(TABLE_ADMIN, null, values)
            db.setTransactionSuccessful()
            adminId != -1L
        } finally {
            db.endTransaction()
        }
    }

    fun validateUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf("id"),
            "username = ? AND password = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        val isValid = cursor.moveToFirst()
        cursor.close()
        return isValid
    }

    fun getAdminIdByUsername(username: String): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf("id"),
            "username = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        val adminId =
            if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("id")) else -1
        cursor.close()
        return adminId
    }

    fun getAdminById(adminId: Int): Admin {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf("id", "name", "email", "username", "password"),
            "id = ?",
            arrayOf(adminId.toString()),
            null,
            null,
            null
        )
        val admin = if (cursor.moveToFirst()) {
            Admin(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            )
        } else {
            throw Exception("Admin not found")
        }
        cursor.close()
        return admin
    }


    fun updatePassword(adminId: Int, oldPassword: String, newPassword: String): Boolean {
        val db = writableDatabase
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf("id"),
            "id = ? AND password = ?",
            arrayOf(adminId.toString(), oldPassword),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val contentValues = ContentValues().apply {
                put("password", newPassword)
            }
            val result = db.update(
                TABLE_ADMIN,
                contentValues,
                "id = ? AND password = ?",
                arrayOf(adminId.toString(), oldPassword)
            )
            cursor.close()
            result > 0
        } else {
            cursor.close()
            false
        }
    }


    fun getAdminNameById(adminId: Int): String? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMIN,
            arrayOf("name"),
            "id = ?",
            arrayOf(adminId.toString()),
            null,
            null,
            null
        )
        val adminName = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow("name"))
        } else {
            null
        }
        cursor.close()
        return adminName
    }

    // Thao tác với bảng Member
    fun addMember(member: Member) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", member.name)
            put("email", member.email)
            put("imageUri", member.imageUri)
        }
        db.insert(TABLE_MEMBER, null, values)
        db.close()
    }

    fun updateMember(member: Member) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", member.name)
            put("email", member.email)
            put("imageUri", member.imageUri)
        }
        db.update(TABLE_MEMBER, values, "id = ?", arrayOf(member.id.toString()))
        db.close()
    }

    fun deleteMember(memberId: Int) {
        val db = writableDatabase
        db.delete(TABLE_MEMBER, "id = ?", arrayOf(memberId.toString()))
        db.close()
    }

    fun getAllMembers(): List<Member> {
        val members = mutableListOf<Member>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEMBER,
            arrayOf("id", "name", "email", "imageUri"),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
            members.add(Member(id, name, email, imageUri))
        }
        cursor.close()
        return members
    }

    fun searchMembers(query: String): List<Member> {
        val members = mutableListOf<Member>()
        val db = this.readableDatabase
        val selection = "name LIKE ? OR id LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        val cursor = db.query("Member", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
                members.add(Member(id, name, email, imageUri))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return members
    }

    // Thêm phương thức để quản lý nhà cung cấp
    fun addSupplier(supplier: Supplier1) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", supplier.name)
        }
        db.insert(TABLE_SUPPLIER, null, values)
        db.close()
    }

    fun updateSupplier(supplier: Supplier1) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", supplier.name)
        }
        db.update(TABLE_SUPPLIER, values, "id = ?", arrayOf(supplier.id.toString()))
        db.close()
    }

    fun deleteSupplier(supplierId: Int) {
        val db = writableDatabase
        db.delete(TABLE_SUPPLIER, "id = ?", arrayOf(supplierId.toString()))
        db.close()
    }

    fun getAllSuppliers(): List<Supplier1> {
        val suppliers = mutableListOf<Supplier1>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SUPPLIER,
            arrayOf("id", "name"),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            suppliers.add(Supplier1(id, name))
        }
        cursor.close()
        return suppliers
    }

    // Thao tác với bảng Category
    fun addCategory(category: Category) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
        }
        db.insert(TABLE_CATEGORY, null, values)
        db.close()
    }

    fun updateCategory(category: Category) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
        }
        db.update(TABLE_CATEGORY, values, "id = ?", arrayOf(category.id.toString()))
        db.close()
    }

    fun deleteCategory(categoryId: Int) {
        val db = writableDatabase
        db.delete(TABLE_CATEGORY, "id = ?", arrayOf(categoryId.toString()))
        db.close()
    }

    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORY,
            arrayOf("id", "name"),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            categories.add(Category(id, name))
        }
        cursor.close()
        return categories
    }

    // Thao tác với bảng Book
    fun addBook(book: Book) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", book.name)
            put("imageUri", book.imageUri)
            put("category_Id", book.categoryId)
            put("quantity", book.quantity)
            put("price", book.price)
        }
        db.insert("book", null, contentValues)
        db.close()
    }


    fun updateBook(book: Book) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", book.name)
            put("imageUri", book.imageUri)
            put("category_Id", book.categoryId)
            put("quantity", book.quantity) // Thêm số lượng
            put("price", book.price)
        }
        db.update("book", contentValues, "id = ?", arrayOf(book.id.toString()))
        db.close()
    }

    fun deleteBook(bookId: Int) {
        val db = writableDatabase
        db.delete(TABLE_BOOK, "id = ?", arrayOf(bookId.toString()))
        db.close()
    }

    fun getAllBooks(): List<Book> {
        val books = mutableListOf<Book>()
        val db = readableDatabase
        val cursor = db.query(
            "book", // Tên bảng
            arrayOf(
                "id",
                "name",
                "imageUri",
                "category_Id",
                "quantity",
                "price"
            ), // Cột cần truy vấn
            null, // Điều kiện WHERE
            null, // Tham số cho điều kiện WHERE
            null, // GROUP BY
            null, // HAVING
            null  // ORDER BY
        )

        // Duyệt qua các kết quả trả về từ con trỏ
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val imageUri = cursor.getString(cursor.getColumnIndex("imageUri"))
            val categoryId = cursor.getInt(cursor.getColumnIndex("category_Id"))
            val quantity = cursor.getInt(cursor.getColumnIndex("quantity"))
            val price = cursor.getDouble(cursor.getColumnIndex("price"))

            books.add(Book(id, name, imageUri, categoryId, quantity, price))
        }

        cursor.close()
        db.close()
        return books
    }


    fun searchBooks(query: String): List<Book> {
        val books = mutableListOf<Book>()
        val db = this.readableDatabase
        val selection = "name LIKE ? OR id LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%")
        val cursor: Cursor? = db.query("book", null, selection, selectionArgs, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val imageUri = cursor.getString(cursor.getColumnIndexOrThrow("imageUri"))
                val categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("category_Id"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))
                books.add(Book(id, name, imageUri, categoryId, quantity, price))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return books
    }


    // Thao tác với bảng BorrowSlip
    fun addBorrowSlip(borrowSlip: BorrowSlip) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("memberId", borrowSlip.memberId)
            put("bookId", borrowSlip.bookId)
            put("borrowDate", borrowSlip.borrowDate)
            put("returnDate", borrowSlip.returnDate)
            put("status", borrowSlip.status)
        }
        db.insert(TABLE_BORROW_SLIP, null, values)
        db.close()
    }

    fun updateBorrowSlip(borrowSlip: BorrowSlip): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("memberId", borrowSlip.memberId)
            put("bookId", borrowSlip.bookId)
            put("borrowDate", borrowSlip.borrowDate)
            put("returnDate", borrowSlip.returnDate)
            put("status", borrowSlip.status)
        }
        val result = db.update(
            TABLE_BORROW_SLIP,
            values,
            "id = ?",
            arrayOf(borrowSlip.id.toString())
        )
        db.close()
        return result > 0
    }


    fun deleteBorrowSlip(borrowSlipId: Int) {
        val db = writableDatabase
        db.delete(TABLE_BORROW_SLIP, "id = ?", arrayOf(borrowSlipId.toString()))
        db.close()
    }

    fun getAllBorrowSlips(): List<BorrowSlip> {
        val borrowSlips = mutableListOf<BorrowSlip>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BORROW_SLIP,
            arrayOf("id", "memberId", "bookId", "borrowDate", "returnDate", "status"),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val memberId = cursor.getInt(cursor.getColumnIndexOrThrow("memberId"))
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow("bookId"))
            val borrowDate = cursor.getString(cursor.getColumnIndexOrThrow("borrowDate"))
            val returnDate = cursor.getString(cursor.getColumnIndexOrThrow("returnDate"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            borrowSlips.add(BorrowSlip(id, memberId, bookId, borrowDate, returnDate, status))
        }
        cursor.close()
        return borrowSlips
    }


    fun getBorrowSlipById(id: Int): BorrowSlip {
        val db = readableDatabase
        val cursor = db.query(
            "BorrowSlip",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor?.moveToFirst()
        val borrowSlip = BorrowSlip(
            id = cursor.getInt(cursor.getColumnIndex("id")),
            memberId = cursor.getInt(cursor.getColumnIndex("memberId")),
            bookId = cursor.getInt(cursor.getColumnIndex("bookId")),
            borrowDate = cursor.getString(cursor.getColumnIndex("borrowDate")),
            returnDate = cursor.getString(cursor.getColumnIndex("returnDate")),
            status = cursor.getString(cursor.getColumnIndex("status"))
        )
        cursor.close()
        return borrowSlip
    }

    fun getMemberById(id: Int): Member? {
        val db = readableDatabase
        val cursor = db.query(
            "Member",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val member = Member(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                imageUri = cursor.getString(cursor.getColumnIndex("imageUri"))
            )
            cursor.close()
            member
        } else {
            cursor.close()
            null
        }
    }

    fun updateBookQuantity(bookId: Int, quantityChange: Int) {
        val currentQuantity = getBookQuantity(bookId)
        val newQuantity = currentQuantity + quantityChange
        val db = writableDatabase
        val values = ContentValues().apply {
            put("quantity", newQuantity)
        }
        db.update("Book", values, "id = ?", arrayOf(bookId.toString()))
    }


    fun getBookQuantity(bookId: Int): Int {
        val db = readableDatabase
        val cursor = db.query(
            "Book", arrayOf("quantity"), "id = ?",
            arrayOf(bookId.toString()), null, null, null
        )
        return if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
        } else {
            0
        }.also { cursor.close() }
    }


    fun addBookOrder(bookOrder: BookOrder): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("bookId", bookOrder.bookId)
            put("supplierId", bookOrder.supplierId)
            put("quantityOrder", bookOrder.quantityOrder)
            put("orderDate", bookOrder.orderDate)
            put("isStocked", if (bookOrder.isStocked) 1 else 0)
        }
        val orderId = db.insert(TABLE_BOOKORDER, null, values)
        db.close()
        return orderId != -1L
    }

    fun updateBookOrder(bookOrder: BookOrder): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("bookId", bookOrder.bookId)
            put("supplierId", bookOrder.supplierId)
            put("quantityOrder", bookOrder.quantityOrder)
            put("orderDate", bookOrder.orderDate)
            put("isStocked", if (bookOrder.isStocked) 1 else 0)
        }
        val result = db.update(
            TABLE_BOOKORDER,
            values,
            "id = ?",
            arrayOf(bookOrder.id.toString())
        )
        db.close()
        return result > 0
    }

    fun deleteBookOrder(orderId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_BOOKORDER, "id = ?", arrayOf(orderId.toString()))
        db.close()
        return result > 0
    }

    fun getAllBookOrders(): List<BookOrder> {
        val bookOrders = mutableListOf<BookOrder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BOOKORDER,
            arrayOf("id", "bookId", "supplierId", "quantityOrder", "orderDate", "isStocked"),
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow("bookId"))
            val supplierId = cursor.getInt(cursor.getColumnIndexOrThrow("supplierId"))
            val quantityOrder = cursor.getInt(cursor.getColumnIndexOrThrow("quantityOrder"))
            val orderDate = cursor.getString(cursor.getColumnIndexOrThrow("orderDate"))
            val isStocked = cursor.getInt(cursor.getColumnIndexOrThrow("isStocked")) == 1
            bookOrders.add(BookOrder(id, bookId, supplierId, quantityOrder, orderDate, isStocked))
        }
        cursor.close()
        db.close()
        return bookOrders
    }

    fun searchBookOrders(query: String): List<BookOrder> {
        val bookOrders = mutableListOf<BookOrder>()
        val db = readableDatabase
        val selection = "id LIKE ? OR bookId LIKE ? OR supplierId LIKE ?"
        val selectionArgs = arrayOf("%$query%", "%$query%", "%$query%")
        val cursor = db.query(
            TABLE_BOOKORDER,
            arrayOf("id", "bookId", "supplierId", "quantityOrder", "orderDate", "isStocked"),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow("bookId"))
            val supplierId = cursor.getInt(cursor.getColumnIndexOrThrow("supplierId"))
            val quantityOrder = cursor.getInt(cursor.getColumnIndexOrThrow("quantityOrder"))
            val orderDate = cursor.getString(cursor.getColumnIndexOrThrow("orderDate"))
            val isStocked = cursor.getInt(cursor.getColumnIndexOrThrow("isStocked")) == 1
            bookOrders.add(BookOrder(id, bookId, supplierId, quantityOrder, orderDate, isStocked))
        }
        cursor.close()
        db.close()
        return bookOrders
    }

    fun getBookOrderById(orderId: Int): BookOrder? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_BOOKORDER,
            arrayOf("id", "bookId", "supplierId", "quantityOrder", "orderDate", "isStocked"),
            "id = ?",
            arrayOf(orderId.toString()),
            null,
            null,
            null
        )

        var bookOrder: BookOrder? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val bookId = cursor.getInt(cursor.getColumnIndexOrThrow("bookId"))
            val supplierId = cursor.getInt(cursor.getColumnIndexOrThrow("supplierId"))
            val quantityOrder = cursor.getInt(cursor.getColumnIndexOrThrow("quantityOrder"))
            val orderDate = cursor.getString(cursor.getColumnIndexOrThrow("orderDate"))
            val isStocked = cursor.getInt(cursor.getColumnIndexOrThrow("isStocked")) > 0

            bookOrder = BookOrder(id, bookId, supplierId, quantityOrder, orderDate, isStocked)
        }

        cursor.close()
        return bookOrder
    }

    fun getBooksBorrowedWithinDateRange(startDate: String, endDate: String): List<BookBorrowStat> {
        val bookBorrowStats = mutableListOf<BookBorrowStat>()
        val db = this.readableDatabase
        val query = """
            SELECT b.id, b.name, b.quantity, b.imageUri, COUNT(bb.bookId) AS borrowcount
            FROM book b
            INNER JOIN BorrowSlip bb ON b.id = bb.bookId
            WHERE bb.borrowDate BETWEEN ? AND ?
            GROUP BY bb.bookId
            ORDER BY borrowcount DESC
            LIMIT 10
        """
        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))
        if (cursor.moveToFirst()) {
            do {
                val bookId = cursor.getInt(cursor.getColumnIndex("id"))
                val bookName = cursor.getString(cursor.getColumnIndex("name"))
                val bookQuantity = cursor.getInt(cursor.getColumnIndex("quantity"))
                val borrowCount = cursor.getInt(cursor.getColumnIndex("borrowcount"))
                val imageUri = cursor.getString(cursor.getColumnIndex("imageUri"))

                val bookBorrowStat =
                    BookBorrowStat(bookId, bookName, imageUri, bookQuantity, borrowCount)
                bookBorrowStats.add(bookBorrowStat)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return bookBorrowStats
    }

}
