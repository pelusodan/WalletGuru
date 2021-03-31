package com.peluso.walletguru.model

/**
 * Class responsible for mapping subreddits to the account types we will suppoprt
 * for users. Each list represents which subreddits are included in the category
 * of each account
 */
enum class AccountType(val subreddits: List<String>, val tableName: String) {
    CREDIT_CARD(listOf("CreditCards", "CRedit"), "credit_card"),
    INVESTMENT(listOf("investment", "investing"), "investment"),
    MORTGAGE(listOf("realestateinvesting", "personalfinance"), "mortgage"),
    CRYPTO(listOf("CryptoCurrency", "Bitcoin", "ethereum"), "crypto"),
    CHECKING(listOf("personalfinance"), "checking"),
    SAVING(listOf("Frugal", "financialindependence", "povertyfinance"), "saving"),
    REALESTATE(listOf("realestateinvesting"), "real_estate")
}

/**
 * Extension function for making an account type from table string (for serialization)
 */
fun String.toAccountType(): AccountType {
    return when (this) {
        AccountType.CREDIT_CARD.tableName -> AccountType.CREDIT_CARD
        AccountType.INVESTMENT.tableName -> AccountType.INVESTMENT
        AccountType.MORTGAGE.tableName -> AccountType.MORTGAGE
        AccountType.CRYPTO.tableName -> AccountType.CRYPTO
        AccountType.CHECKING.tableName -> AccountType.CHECKING
        AccountType.SAVING.tableName -> AccountType.SAVING
        AccountType.REALESTATE.tableName -> AccountType.REALESTATE
        //TODO: make a better null checker here
        else -> AccountType.REALESTATE
    }
}