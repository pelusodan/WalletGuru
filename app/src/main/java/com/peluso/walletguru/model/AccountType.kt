package com.peluso.walletguru.model

/**
 * Class responsible for mapping subreddits to the account types we will suppoprt
 * for users. Each list represents which subreddits are included in the category
 * of each account
 */
enum class AccountType(val subreddits: List<String>) {
    CREDIT_CARD(listOf("CreditCards", "CRedit")),
    INVESTMENT(listOf("investment", "investing")),
    MORTGAGE(listOf("realestateinvesting", "personalfinance")),
    CRYPTO(listOf("CryptoCurrency", "Bitcoin", "ethereum")),
    CHECKING(listOf("personalfinance")),
    SAVING(listOf("Frugal", "financialindependence", "povertyfinance")),
    REALESTATE(listOf("realestateinvesting"))
}