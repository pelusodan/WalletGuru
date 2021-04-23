# WalletGuru

![WalletGuruLogo](WalletGuruLogo.png)

### Goals

WalletGuru is a finance-based Reddit client designed to show users the most relevant information to their current account balance performance.

#### Features
- Favorite posts to save in your phone's local database
- Swipe posts away from the main feed
- Location based Subreddits
- Balance update with full ledger for display
- Open link posts in an external browser intent
- Link posts appear as webviews in expanded post view
- Adding accounts

#### Our supported financial account types include
- Crypto
- Investment
- Real Estate
- Savings
- Checking
- Credit Card

Track any of these accounts to get Subreddits carefully selected to align with the topic of interest.

#### Location Based Finance Subreddits
Enable location permissions to allow for location-based finance subreddits to appear in your feed. Now supporting:
- USA
- India
- Philippines
- UK
- Canada
- Australia

### Running the project
Our app is dependent on the [Kirk Bushman ARAW Library](https://github.com/KirkBushman/ARAW), which requires providing a client Reddit Id to use their API.

1. Create a Reddit Account
2. Create a new application on [this](https://www.reddit.com/prefs/apps) page
3. Copy the client ID
4. Create a new file `app/src/main/res/values/secrets.xml`
5. Add the following to your `secrets.xml` file
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="reddit_client_id">YOUR_CLIENT_ID</string>
</resources>
```

### Developed for NEU CS5520 by
- [Dan Peluso](https://github.com/pelusodan)
- [Jurgen Fataj](https://github.com/JFATAJ)
- [Mao Liang Liu](https://github.com/maoliang1234)

##### Special Thanks
Dan Feinberg for design help and recommendations

### License
This project is licensed under the MIT License


