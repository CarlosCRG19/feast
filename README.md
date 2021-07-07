Original App Design Project - README Template
===

# Restaurants Tinder

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
An app that gamifies and optimizes the decision process for the best restaurant that meets the characteristics specified by the user. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** travel, food discovery
- **Mobile:** mobile first experience
- **Story:** In the main activity, users will see recommendations for restaurants near them and can swipe left to ignore them or right to move them to a next phase of elimination among other selected restaurants.
- **Market:** Tourists, General public (people with access to an smartphone)
- **Habit:** Users can discover new restaurants near them (they can see their information if they swipe up). Users can select and see their favorite restaurants.
- **Scope:** At the beginning, the app is only directed towards exploration by the user, however, it can be expanded to generate partnerships with restaurants to offer coupons or promotions to users who visit them regularly or mark them as favorites. Also, the user could request an Uber through the application to reach the selected place. Finally, a feature can be added to invite friends to a selected restaurant (or even to be able to choose a restaurant among several people).

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The user can see a secuence of cards with restaurants near him
* User can swipe right to select a restaurant (and left to ignore another)
* User can filter the displayed restaurants based on characteristics (price, location, rating, etc.)
* User can favorite a restaurant (and favorited restaurants will be stored in a database) 
* User can compare selected restaurants

**Optional Nice-to-have Stories**

* User can write a review and this will be posted on Yelp
* A Google Maps route is displayed, which connects the user location with the restaurant location

### 2. Screen Archetypes

* Login
   * User can login into existing account
* Registration Screen
   * User can create new account
* Stream
    * User can see a sequence of cards with restaurants near the specified location
    * User can swipe left or right to select or ignore restaurants (up to 5 restaurants to the right)
    * User can view cards of restaurants that match the filters
    * User can swipe up to see details of restaurants
    * User can favorite restaurants
* Filter screen
    * User can select different characteristics of the restaurants that they want to see in the main stream.
* Detail screen
    * User can see specific information of a restaurant
* Compare screen
    * User can choose specific atributes to sort the selected restaurants 
* Favorite screen
    * User can see a list of restaurants that have been favorited   

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream
* Compare screen
* Favorites screen

**Flow Navigation** (Screen to Screen)

* Login Screen
   => Home
* Registration Screen
   => Home
* Stream screen
   => Filters screen
   => Details screen
* Detail screen
   => None
* Filters screen
   => Stream screen
* Comparison screen
   => None
