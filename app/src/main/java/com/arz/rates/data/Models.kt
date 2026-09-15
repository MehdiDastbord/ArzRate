package com.arz.rates.data

import kotlinx.serialization.Serializable

@Serializable
data class Rate(
    val value: String? = null,
    val change: Double? = null,
    val timestamp: Long? = null,
    val date: String? = null,
    val change_val: Double? = null,
    val change_pct: Double? = null
) {
    fun numericValue(): Double? = value?.replace(",", "")?.toDoubleOrNull()
}

@Serializable
data class RateItem(
    val key: String,
    val rate: Rate
) {
    val title: String get() = CurrencyNames.nameFor(key, Language.ENGLISH)
    val symbol: String get() = CurrencyNames.symbolFor(key)
}

enum class Language { ENGLISH, PERSIAN }

object CurrencyNames {
    private val names = mapOf(
        "usd" to "US Dollar", "eur" to "Euro", "gbp" to "British Pound",
        "aed" to "UAE Dirham", "sar" to "Saudi Riyal", "qar" to "Qatari Riyal",
        "kwd" to "Kuwaiti Dinar", "omr" to "Omani Rial", "bhd" to "Bahraini Dinar",
        "try" to "Turkish Lira", "jpy" to "Japanese Yen", "cny" to "Chinese Yuan",
        "inr" to "Indian Rupee", "cad" to "Canadian Dollar", "aud" to "Australian Dollar",
        "chf" to "Swiss Franc", "nzd" to "New Zealand Dollar", "rub" to "Russian Ruble",
        "krw" to "South Korean Won", "thb" to "Thai Baht", "myr" to "Malaysian Ringgit",
        "sek" to "Swedish Krona", "nok" to "Norwegian Krone", "dkk" to "Danish Krone",
        "pln" to "Polish Zloty", "zar" to "South African Rand", "mxn" to "Mexican Peso",
        "brl" to "Brazilian Real", "sgd" to "Singapore Dollar", "hkd" to "Hong Kong Dollar",
        "idr" to "Indonesian Rupiah", "pkr" to "Pakistani Rupee", "iqd" to "Iraqi Dinar",
        "afn" to "Afghan Afghani", "xau" to "Gold", "xag" to "Silver",
        "usd_xau" to "Gold / USD", "bub_sekkeh" to "Emami Coin",
        "bub_bahar" to "Bahar Azadi Coin", "bub_nim" to "Half Coin",
        "bub_rob" to "Quarter Coin", "bub_18ayar" to "18K Gold",
        "bub_gerami" to "Gerami Coin", "mob_usd" to "Mobile USD",
        "mob_gbp" to "Mobile GBP", "mob_eur" to "Mobile EUR", "mob_aed" to "Mobile AED"
    )

    private val persianNames = mapOf(
        "usd" to "دلار آمریکا", "eur" to "یورو", "gbp" to "پوند انگلیس",
        "aed" to "درهم امارات", "sar" to "ریال سعودی", "qar" to "ریال قطر",
        "kwd" to "دینار کویت", "omr" to "ریال عمان", "bhd" to "دینار بحرین",
        "try" to "لیر ترکیه", "jpy" to "ین ژاپن", "cny" to "یوان چین",
        "inr" to "روپیه هند", "cad" to "دلار کانادا", "aud" to "دلار استرالیا",
        "chf" to "فرانک سوئیس", "nzd" to "دلار نیوزیلند", "rub" to "روبل روسیه",
        "krw" to "وون کره جنوبی", "thb" to "بات تایلند", "myr" to "رینگیت مالزی",
        "sek" to "کرون سوئد", "nok" to "کرون نروژ", "dkk" to "کرون دانمارک",
        "pln" to "زلوتی لهستان", "zar" to "رند آفریقای جنوبی", "mxn" to "پزو مکزیک",
        "brl" to "رئال برزیل", "sgd" to "دلار سنگاپور", "hkd" to "دلار هنگ‌کنگ",
        "idr" to "روپیه اندونزی", "pkr" to "روپیه پاکستان", "iqd" to "دینار عراق",
        "afn" to "افغانی", "xau" to "طلا", "xag" to "نقره",
        "usd_xau" to "طلا / دلار", "bub_sekkeh" to "سکه امامی",
        "bub_bahar" to "سکه بهار آزادی", "bub_nim" to "نیم سکه",
        "bub_rob" to "ربع سکه", "bub_18ayar" to "طلای ۱۸ عیار",
        "bub_gerami" to "سکه گرمی", "mob_usd" to "دلار موبایل",
        "mob_gbp" to "پوند موبایل", "mob_eur" to "یورو موبایل", "mob_aed" to "درهم موبایل"
    )

    private val apiPersianNames = mapOf(
        "usd_usdt" to "تتر", "dolar_harat_sell" to "دلار هرات (فروش)",
        "harat_naghdi_sell" to "دلار هرات نقدی (فروش)", "harat_naghdi_buy" to "دلار هرات نقدی (خرید)",
        "sekkeh" to "سکه امامی", "bahar" to "سکه بهار آزادی", "nim" to "نیم سکه",
        "rob" to "ربع سکه", "abshodeh" to "آبشده", "gerami" to "سکه گرمی", "18ayar" to "طلای ۱۸ عیار",
        "usd_sell" to "دلار آمریکا (فروش)", "usd_buy" to "دلار آمریکا (خرید)",
        "usd_farda_sell" to "دلار فردایی (فروش)", "usd_farda_buy" to "دلار فردایی (خرید)",
        "dirham_dubai" to "درهم دبی", "aed_sell" to "درهم امارات (فروش)", "aed_note" to "درهم اسکناس",
        "eur_hav" to "یورو حواله", "gbp_hav" to "پوند حواله", "aud_hav" to "دلار استرالیا حواله",
        "myr_hav" to "رینگیت مالزی حواله", "cny_hav" to "یوان چین حواله", "try_hav" to "لیر ترکیه حواله",
        "jpy_hav" to "ین ژاپن حواله", "cad_cash" to "دلار کانادا نقدی", "usd_pp" to "دلار پی‌پی",
        "eur_pp" to "یورو پی‌پی", "usd_btc" to "بیت‌کوین / دلار", "usd_eth" to "اتریوم / دلار"
    )

    private val isoPersianNames = mapOf(
        "afn" to "افغانی افغانستان", "all" to "لک آلبانی", "amd" to "درام ارمنستان", "ang" to "گیلدر آنتیل هلند",
        "aoa" to "کوانزای آنگولا", "ars" to "پزو آرژانتین", "awg" to "فلورین آروبا", "azn" to "منات آذربایجان",
        "bam" to "مارک بوسنی", "bbd" to "دلار باربادوس", "bdt" to "تاکای بنگلادش", "bgn" to "لو بلغارستان",
        "bif" to "فرانک بوروندی", "bmd" to "دلار برمودا", "bnd" to "دلار برونئی", "bob" to "بولیویانو",
        "brl" to "رئال برزیل", "bsd" to "دلار باهاما", "btn" to "نگولتروم بوتان", "bwp" to "پولا بوتسوانا",
        "byn" to "روبل بلاروس", "bzd" to "دلار بلیز", "cdf" to "فرانک کنگو", "clp" to "پزو شیلی",
        "cnh" to "یوان چین (آف‌شور)", "cop" to "پزو کلمبیا", "crc" to "کولون کاستاریکا", "cuc" to "پزوی قابل تبدیل کوبا",
        "cup" to "پزوی کوبا", "cve" to "اسکودوی کیپ ورد", "czk" to "کرون جمهوری چک", "djf" to "فرانک جیبوتی",
        "dop" to "پزوی دومینیکن", "dzd" to "دینار الجزایر", "egp" to "پوند مصر", "ern" to "ناکفای اریتره",
        "etb" to "بیر اتیوپی", "fjd" to "دلار فیجی", "fkp" to "پوند جزایر فالکلند", "gel" to "لاری گرجستان",
        "ghs" to "سدی غنا", "gmd" to "دالاسی گامبیا", "gnf" to "فرانک گینه", "gtq" to "کتزال گواتمالا",
        "gyd" to "دلار گویان", "hkd" to "دلار هنگ‌کنگ", "hnl" to "لمپیرا هندوراس", "hrk" to "کونا کرواسی",
        "htg" to "گورد هائیتی", "huf" to "فورینت مجارستان", "idr" to "روپیه اندونزی", "ils" to "شکل اسرائیل",
        "imp" to "پوند من", "inr" to "روپیه هند", "iqd" to "دینار عراق", "isk" to "کرون ایسلند",
        "jmd" to "دلار جامائیکا", "jod" to "دینار اردن", "jpy" to "ین ژاپن", "kes" to "شیلینگ کنیا",
        "kgs" to "سوم قرقیزستان", "khr" to "ریل کامبوج", "kmf" to "فرانک کومور", "kpw" to "وون کره شمالی",
        "krw" to "وون کره جنوبی", "kzt" to "تنگه قزاقستان", "lak" to "کیپ لائوس", "lbp" to "لیره لبنان",
        "lkr" to "روپیه سریلانکا", "lrd" to "دلار لیبریا", "lsl" to "لوتی لسوتو", "lyd" to "دینار لیبی",
        "mad" to "درهم مراکش", "mdl" to "لئوی مولداوی", "mga" to "آریاری ماداگاسکار", "mkd" to "دینار مقدونیه",
        "mmk" to "کیات میانمار", "mnt" to "توگروگ مغولستان", "mop" to "پاتاکای ماکائو", "mru" to "اوگوئیای موریتانی",
        "mur" to "روپیه موریس", "mvr" to "روفیای مالدیو", "mwk" to "کواچای مالاوی", "mxn" to "پزو مکزیک",
        "myr" to "رینگیت مالزی", "mzn" to "متیکال موزامبیک", "nad" to "دلار نامیبیا", "ngn" to "نایرای نیجریه",
        "nio" to "کوردوبای نیکاراگوئه", "nok" to "کرون نروژ", "npr" to "روپیه نپال", "nzd" to "دلار نیوزیلند",
        "omr" to "ریال عمان", "pab" to "بالبوای پاناما", "pen" to "سول پرو", "pgk" to "کینای پاپوا گینه نو",
        "php" to "پزوی فیلیپین", "pkr" to "روپیه پاکستان", "pln" to "زلوتی لهستان", "pyg" to "گوارانی پاراگوئه",
        "qar" to "ریال قطر", "ron" to "لئوی رومانی", "rsd" to "دینار صربستان", "rub" to "روبل روسیه",
        "rwf" to "فرانک رواندا", "sar" to "ریال سعودی", "sbd" to "دلار جزایر سلیمان", "scr" to "روپیۀ سیشل",
        "sdg" to "پوند سودان", "sek" to "کرون سوئد", "sgd" to "دلار سنگاپور", "shp" to "پوند سنت هلن",
        "sle" to "لئون سیرالئون", "sll" to "لئون سیرالئون", "sos" to "شیلینگ سومالی", "srd" to "دلار سورینام",
        "ssp" to "پوند سودان جنوبی", "stn" to "دوبرا سائوتومه", "svc" to "کولون السالوادور", "syp" to "لیره سوریه",
        "szl" to "لیلانگنی اسواتینی", "thb" to "بات تایلند", "tjs" to "سامانی تاجیکستان", "tmt" to "منات ترکمنستان",
        "tnd" to "دینار تونس", "top" to "پاآنگای تونگا", "try" to "لیر ترکیه", "ttd" to "دلار ترینیداد و توباگو",
        "twd" to "دلار تایوان", "tzs" to "شیلینگ تانزانیا", "uah" to "گریونا اوکراین", "ugx" to "شیلینگ اوگاندا",
        "uyu" to "پزوی اروگوئه", "uzs" to "سوم ازبکستان", "ves" to "بولیوار ونزوئلا", "vnd" to "دونگ ویتنام",
        "vuv" to "واتوی وانواتو", "wst" to "تالای ساموآ", "xaf" to "فرانک آفریقای مرکزی", "xcd" to "دلار کارائیب شرقی",
        "xof" to "فرانک آفریقای غربی", "xpf" to "فرانک اقیانوس آرام", "yer" to "ریال یمن", "zar" to "رند آفریقای جنوبی",
        "zmw" to "کواچای زامبیا", "zwl" to "دلار زیمبابوه", "aed" to "درهم امارات", "bhd" to "دینار بحرین",
        "cad" to "دلار کانادا", "chf" to "فرانک سوئیس", "cny" to "یوان چین", "dkk" to "کرون دانمارک",
        "eur" to "یورو", "gbp" to "پوند انگلیس", "kwd" to "دینار کویت", "usd" to "دلار آمریکا",
        "aud" to "دلار استرالیا", "brl" to "رئال برزیل", "xau" to "طلا", "xag" to "نقره",
        "btc" to "بیت‌کوین", "eth" to "اتریوم", "xrp" to "ریپل", "bch" to "بیت‌کوین کش",
        "ltc" to "لایت‌کوین", "eos" to "ایاس", "bnb" to "بایننس کوین", "dash" to "دش", "doge" to "دوج‌کوین",
        "sol" to "سولانا", "ada" to "کاردانو", "shib" to "شیبا اینو", "avax" to "آوالانچ", "matic" to "پالیگان",
        "dot" to "پولکادات", "xlm" to "استلار", "ton" to "تون‌کوین", "trx" to "ترون", "uni" to "یونی‌سواپ",
        "link" to "چین‌لینک", "atom" to "کازماس", "xmr" to "مونرو", "etc" to "اتریوم کلاسیک", "fil" to "فایل‌کوین",
        "icp" to "اینترنت کامپیوتر", "hbar" to "هدرا", "vet" to "وی‌چین", "near" to "نیر", "qnt" to "کوانت",
        "mkr" to "میکر", "aave" to "آوه", "grt" to "گراف", "algo" to "الگوراند", "axs" to "اکسی اینفینیتی",
        "stx" to "استکس", "egld" to "ال‌روند", "sand" to "سندباکس", "theta" to "تتا"
    )

    fun nameFor(key: String, language: Language = Language.ENGLISH): String {
        val fallbackEnglish = key.replace("_", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
        return if (language == Language.PERSIAN) {
            apiPersianNames[key]
                ?: isoPersianNames[key]
                ?: persianNames[key]
                ?: names[key]
                ?: runCatching {
                    java.util.Currency.getInstance(key.uppercase())
                        .getDisplayName(java.util.Locale.forLanguageTag("fa-IR"))
                }.getOrNull()
                ?: "ارز ${key.uppercase()}"
        } else {
            names[key] ?: fallbackEnglish
        }
    }

    fun symbolFor(key: String): String = when (key) {
        "usd" -> "$"; "eur" -> "€"; "gbp" -> "£"; "aed" -> "د.إ"
        "sar" -> "﷼"; "jpy" -> "¥"; "cny" -> "¥"; "inr" -> "₹"
        else -> key.uppercase()
    }
}
