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

* User can login and logout
* User can navigate to Explore, History or Profile views
* In Explore view, user can create a new visit
  * User can select location
  * User can select a date
* User can see a sequence of cards with restaurants near them
* User can swipe right to select a restaurant (and left to ignore another)
* User can swipe up to see details of restaurants
* User can filter the displayed restaurants based on characteristics (price, location, rating, etc.)
* User can compare selected restaurants
* User can select "Go now!" button to select a restaurant and finish the search
* User can see next visits and past visits (Visits stored in a database)
* User can favorite a restaurant and see it in profile view


**Optional Nice-to-have Stories**

* User can write a review and this will be posted on Yelp
* User can select "random pick" to select a restaurant
* User can create a new visit from detail view
* A Google Maps route is displayed, which connects the user location with the restaurant location

### 2. Screen Archetypes

* Login
   * User can login into existing account
* Registration Screen
   * User can create new account
* Explore screen
   * User can create a new visit      
         * User can select a date
         * User can select location
   * User can see a sequence of cards with restaurants near them
   * User can swipe right to select a restaurant (and left to ignore another)
   * User can swipe up to see details of restaurants
   * User can only see restaurants that match the specified filters
* Filters screen
   * User can select different characteristics of the restaurants that they want to see in the main stream.
* Detail screen
    * User can see specific information of a restaurant
    * User can select "Go now!" (if they come from Explore screen) or "Create visit!" (if they come from history or profile screen) to instantly create a visit without any steps further
* Compare screen
    * User can choose specific atributes to sort the selected restaurants  
    * User can click on "Go!" button to finally select a restaurant and conclude the process
* History screen
    * User can see next restaurant visits
    * User can see past restaurant visits
    * User can click on any visit to see the restaurant's detail screen
* Profile screen
    * User can see their information (username and email)
    * User can logout
    * User can see favorite restaurants
    * User can click on favorited restaurants to open details for that restaurant 
        
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Login Screen
* Registration Screen
* Explore Screen
* Filters Screen
* Detail Screen
* History Screen
* Profile Screen
* Compare screen
* Favorites screen

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Explore Screen
* Registration Screen
   * Explore Screen
* Explore Screen
   * Details Screen
   * Filters Screen
   * Compare Screen
* Filters Screen
   * Explore Screen
* Details Screen
   * None 
* Compare screen
   * None
* History Screen
   * Details Screen
* Profile Screen
   * Details Screen 

# Wireframes
_Part 1_
<img src='wireframes.png' title='First Wireframe' width='' alt='First wireframe' />
_Part 2_
<img src='wireframes_2.png' title='Second Wireframe' width='' alt='Second wireframe' />

# Schema 
### Model: Restaurant

| Property | Type | Description |
| --- | --- | --- |
| objectID | String | Unique identifier for the Restaurant (default field) |
| name | String | Name of this business. | 
| imageUrl | String | URL of photo for this business. | 
| telephone | String | Phone number of the business | 
| rating | float | Rating for this business (value ranges from 1, 1.5, ... 4.5, 5). | 
| price | String | Price level of the business. Value is one of $, $$, $$$ and $$$$. |  
| categories | Array | list of strings that represent the categories that the restaurant match | 

### Model: Location

| Property | Type | Description |
| --- | --- | --- |
| objectID | String | Unique identifier for the location (default field) |
| address | String | Street address of this business. | 
| city | String | City of this business. | 
| country | String | ISO 3166-1 alpha-2 country code of this business. | 
| latitude | float | The latitude of this business. | 
| longitude | float | The longitude of this business. | 
| restaurant | Pointer to Restaurant | Reference to restaurant object for this location | 

### Model: Hour

| Property | Type | Description |
| --- | --- | --- |
| objectID | String | Unique identifier for the location (default field) |
| day | int | From 0 to 6, representing day of the week from Monday to Sunday. Notice that you may get the same day of the week more than once if the business has more than one opening time slots. | 
| open | String | Start of the opening hours in a day, in 24-hour clock notation, like 1000 means 10 AM. | 
| end | String | End of the opening hours in a day, in 24-hour clock notation, like 2130 means 9:30 PM. | 
| restaurant | Pointer to Restaurant | Reference to restaurant object for this location | 

### Model: User
| Property | Type | Description |
| --- | --- | --- |
| objectID | String | unique identifier for the Restaurant (default field) |
| createdAt | DateTime | date when user is created |
| username | String | name of the user |
| email | String | user's email |
| password | String | user's password |

### Model: Visit
| Property | Type | Description |
| --- | --- | --- |
| objectID | String | unique identifier for the Restaurant (default field) |
| createdAt | DateTime | date when visit is created |
| user | Pointer to User | user that liked the restaurant |
| restaurant | Pointer to Restaurant | restaurant that has been selected |

### Model: Like
| Property | Type | Description |
| --- | --- | --- |
| objectID | String | Unique identifier for the Restaurant (default field) |
| createdAt | DateTime | date when like is created |
| user | Pointer to User | User that liked the restaurant
| restaurant | Pointer to Restaurant | Restaurant that has been selected |

