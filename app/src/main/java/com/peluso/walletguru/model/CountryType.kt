package com.peluso.walletguru.model

/**
 *Class responsible for mapping country (from location) to subreddits (in an enumerator
 */
enum class CountryType(
    override val subreddits: List<String>,
    val country: String,
    override val tableName: String
) : PostType {
    USA(listOf("wallstreetbets", "FinancialPlanning"), "United States", "UnitedStates"),
    IND(listOf("IndiaInvestments"), "India", "India"),
    UK(
        listOf("UKPersonalFinance", "eupersonalfinance", "UKInvesting", "BitcoinUK"),
        "United Kingdom",
        "UnitedKingdom"
    ),
    AUS(listOf("ausstocks", "BitcoinAUS"), "Australia", "Australia"),
    CAN(listOf("CanadianInvestor", "PersonalFinanceCanada", "BitcoinCA"), "Canada", "Canada"),
    PHI(listOf("phinvest"), "Philippines", "Philippines");

    companion object {
        fun fromString(string: String?): CountryType? {
            return when (string) {
                USA.country -> USA
                UK.country -> UK
                AUS.country -> AUS
                IND.country -> IND
                CAN.country -> CAN
                PHI.country -> PHI
                else -> null
            }
        }

        val allSubreddits: List<String>
            get() {
                val countries = mutableListOf(USA, IND, UK, AUS, CAN, PHI)
                val subreddits = mutableListOf<String>()
                countries.forEach { countryType ->
                    countryType.subreddits.forEach {
                        subreddits.add(it)
                    }
                }
                return subreddits
            }
    }
}