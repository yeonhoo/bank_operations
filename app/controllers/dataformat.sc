val nums = (1 to 5).toArray

nums.sliding(2).toList.foreach( e => println(e))


import org.joda.time.DateTime

val list = List((DateTime.parse("2010-07-06"), 2000),
  (DateTime.parse("2011-07-06"), 3000),
  (DateTime.parse("2012-07-06"),-200),
  (DateTime.parse("2013-07-06"), 300),
  (DateTime.parse("2014-07-06"),-600),
  (DateTime.parse("2015-07-06"),400)
)

list.tail

