package com.peluso.walletguru.model

import kotlin.collections.ArrayList

/**
 * Class responsible for mapping subreddits to the account types we will suppoprt
 * for users. Each list represents which subreddits are included in the category
 * of each account
 */
enum class AccountType(
    override val subreddits: List<String>,
    override val tableName: String,
    val viewName: String
) : PostType {
    CREDIT_CARD(listOf("CreditCards", "CRedit"), "credit_card", "Credit Card"),
    INVESTMENT(listOf("investment", "investing"), "investment", "Investment"),
    MORTGAGE(listOf("realestateinvesting", "personalfinance"), "mortgage", "Mortgage"),
    CRYPTO(listOf("CryptoCurrency", "Bitcoin", "ethereum"), "crypto", "Crypto"),
    CHECKING(listOf("personalfinance"), "checking", "Checking"),
    SAVING(listOf("Frugal", "financialindependence", "povertyfinance"), "saving", "Savings"),
    REALESTATE(listOf("realestateinvesting"), "real_estate", "Real Estate");

    companion object {
        fun getAllTypes(): List<String> {
            val list = ArrayList<String>()
            list.add(CREDIT_CARD.tableName)
            list.add(INVESTMENT.tableName)
            list.add(MORTGAGE.tableName)
            list.add(CRYPTO.tableName)
            list.add(CHECKING.tableName)
            list.add(SAVING.tableName)
            list.add(REALESTATE.tableName)
            return list
        }

        fun getViewNameFromTableName(tableName: String): String {
            return when (tableName) {
                CREDIT_CARD.tableName -> CREDIT_CARD.viewName
                INVESTMENT.tableName -> INVESTMENT.viewName
                MORTGAGE.tableName -> MORTGAGE.viewName
                CRYPTO.tableName -> CRYPTO.viewName
                CHECKING.tableName -> CHECKING.viewName
                SAVING.tableName -> SAVING.viewName
                REALESTATE.tableName -> REALESTATE.viewName
                else -> tableName
            }
        }
    }
}

/**
 * Extension function for making an account type from table string (for serialization)
 */
fun String.toAccountType(): AccountType {
    return when (this) {
        AccountType.CREDIT_CARD.viewName -> AccountType.CREDIT_CARD
        AccountType.INVESTMENT.viewName -> AccountType.INVESTMENT
        AccountType.MORTGAGE.viewName -> AccountType.MORTGAGE
        AccountType.CRYPTO.viewName -> AccountType.CRYPTO
        AccountType.CHECKING.viewName -> AccountType.CHECKING
        AccountType.SAVING.viewName -> AccountType.SAVING
        AccountType.REALESTATE.viewName -> AccountType.REALESTATE
        else -> AccountType.REALESTATE
    }
}
