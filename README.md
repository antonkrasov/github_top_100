
# Decision-making process

After I checked the task I thought it's pretty clear.

The next thing I did is checked Github API, but surprisingly, there is no way to find a list
of the most starred GitHub Repos without search query.

And I don't see anything about search query in test task, so assumed I need to find
the most starred repos thought all github.

I found a way to do this by visiting this page:
https://github.com/search?q=stars%3A%3E100&s=stars&type=Repositories

But it's clear that I should use: Github REST api​ ​https://developer.github.com/v3/​ and parsing HTML
is not an option.

As a result, I decided just to go with 'Android' search query.

The next challenge was how to find top contributors.

At this point I tested everything with Postman and found this way to find top contributors:
https://api.github.com/repos/playframework/playframework/contributors?q=contributions&order=desc

We don't have this info in the Repository model Github API return to us during the search, so we
need to make another API call for every repo we have.

This makes this task much more complex, in real life, I would ask backend developers to create an
optimized API call for us. Because making 1+100 (or N) network calls to display a list doesn't
sound like a good idea.

Sure we can just use RxJava and make this 100+1 calls, and display progressbar during this load,
but what if we would need to display a list of 1000 items, and it doesn't sound like good UX?

So I decided to go this way:

1) We load a list of repos
2) We save it locally
3) We start processing repos one by one and load top contributors asynchronously

This will give us these benefits:
1) Users will see part of the data fast enough (better UX)
2) Much more optimized use of resources
3) Much more scalable solution

If user will close the app and open it again, this will also give us an ability to display
cached data.

