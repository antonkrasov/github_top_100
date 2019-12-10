
# Decision-making process

After I checked the task I thought it's pretty clear.

The next thing I did is checked Github API, but surprisingly, there is no way to find a list
of the most starred GitHub Repos without some specific search query.

And I don't see anything about search query in test task, so assumed I need to find
the most starred repos thought all github.

I found a way to do this by visiting this page:
https://github.com/search?q=stars%3A%3E100&s=stars&type=Repositories

Seems like it's exactly what we need.

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
cached data, this request is very heavy, so it's definitely must-have.

## Architecture

It's very simple and straightforward, I simplified everything, so I don't have hundreds of classes in the simple demo app.

Main work is done in `com.antonkrasov.githubtop100.data.repositories.ReposRepository`.

1. Once someone calls for repos(), we check if we need to load new data or not.

2. After a successful data update, we store timestamp, and we won't try to
load new data before our cache will expire. One minute is setup for now.

3. Once data is loaded we store everything in Sqlite with help of Room.

4. We receive callback from room with new data in 2 places, from the first place we return it to the
ViewMode.

5. From the second place we start loading top contributors info.

## Dependencies

Everything is very common:

1) AndroidX architecture components + Room.
2) Retrofit for network calls. (+adapters and converters)
3) Timber for logging
4) RxJava for easier life :)

## What to improve and work on next

1) Better error handling during load of top contributors
2) DI setup
3) Tests setup (after DI would be easier)
4) Contributors as separate table (will give us a chance for more optimizations)
5) Better way to diff new data
6) Maybe think about a way to listen for individual items change, not full list, as with more items
it would be much less effective to compare full list all the time.

## Interesting problem

```
<-- 403 Forbidden https://api.github.com/repos/torvalds/linux/contributors?q=contributions&order=desc (114ms)
D/OkHttp: {"message":"The history or contributor list is too large to list contributors for
this repository via the API.","documentation_url":"https://developer.github.com/v3/repos/#list-contributors"}
D/OkHttp: <-- END HTTP (191-byte body)
```

Got this error :) So for linux we won't display top contributor.


## Final words

Thanks for this task, I enjoyed it. I think we have lots of things to discuss based on the results :)
Will be happy to hear feedback.
