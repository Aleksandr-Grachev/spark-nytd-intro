package app

package object models {

  import java.time.Instant

  /** VendorIDA
    *
    * @param Airport_fee is $1.25 for pick up only at LaGuardia and John F. Kennedy Airports
    * @param congestion_surcharge Total amount collected in trip for NYS congestion surcharge
    * @param DOLocationID TLC Taxi Zone in which the taximeter was disengaged
    * @param PULocationID LC Taxi Zone in which the taximeter was engaged
    * @param extra Miscellaneous extras and surcharges.
    *         Currently, this only includes the $0.50 and $1 rush hour and overnight charges
    * @param fare_amount The time-and-distance fare calculated by the meter.
    * @param improvement_surcharge $0.30 improvement surcharge assessed trips at the flag drop.
    *                                 The improvement surcharge began being levied in 2015.
    * @param mta_tax $0.50 MTA tax that is automatically triggered based on the metered rate in use
    * @param passenger_count The number of passengers in the vehicle. This is a driver-entered value
    * @param payment_type A numeric code signifying how the passenger paid for the trip.
    *                    1= Credit card
    *                    2= Cash
    *                    3= No charge
    *                    4= Dispute
    *                    5= Unknown
    *                    6= Voided trip
    *
    * @param RatecodeID The final rate code in effect at the end of the trip.
    *                  1= Standard rate
    *                  2=JFK
    *                  3=Newark
    *                  4=Nassau or Westchester
    *                  5=Negotiated fare
    *                  6=Group ride
    * //param Rec TODO:???
    * @param store_and_fwd_flag This flag indicates whether the trip record was held in vehicle
    *                           memory before sending to the vendor, aka “store and forward,”
    *                           because the vehicle did not have a connection to the server.
    *                             Y= store and forward trip
    *                             N= not a store and forward trip
    * @param tip_amount This field is automatically populated for credit card tips. Cash tips are not included.
    * @param tolls_amount Total amount of all tolls paid in trip
    * @param total_amount The total amount charged to passengers. Does not include cash tips.
    * @param tpep_dropoff_datetime The date and time when the meter was engaged.
    * @param tpep_pickup_datetime The date and time when the meter was disengaged
    * @param trip_distance The elapsed trip distance in miles reported by the taximeter.
    * @param VendorID code indicating the TPEP provider that provided the record.
    */
  case class YellowTripData( //2024 - 2011 years
    Airport_fee:           Double,
    congestion_surcharge:  Double,
    DOLocationID:          Long,
    extra:                 Double,
    fare_amount:           Double,
    improvement_surcharge: Double,
    mta_tax:               Option[Double],
    passenger_count:       Int,
    payment_type:          Option[Long],
    PULocationID:          Long,
    RatecodeID:            Long,
    store_and_fwd_flag:    Option[String],
    tip_amount:            Double,
    tolls_amount:          Double,
    total_amount:          Double,
    tpep_dropoff_datetime: Option[Instant],
    tpep_pickup_datetime:  Option[Instant],
    trip_distance:         Double,
    VendorID:              Option[Long]
  )

  case class YellowTripData_10_09( //2010 - 2009 years
    vendor_id:          Option[Long],
    pickup_datetime:    Option[Instant],
    dropoff_datetime:   Option[Instant],
    passenger_count:    Int,
    trip_distance:      Double,
    pickup_longitude:   Double,
    pickup_latitude:    Double,
    rate_code:          Long,
    store_and_fwd_flag: Option[String],
    dropoff_longitude:  Double,
    dropoff_latitude:   Double,
    payment_type:       Option[Long],
    fare_amount:        Double,
    surcharge:          Double,
    mta_tax:            Option[Double],
    tip_amount:         Double,
    tolls_amount:       Double,
    total_amount:       Double
  )

  case class GreenTripData(
    VendorID:              Option[Long],
    lpep_pickup_datetime:  Instant, //The date and time when the meter was engaged.
    lpep_dropoff_datetime: Instant, //The date and time when the meter was disengaged
    store_and_fwd_flag:    Option[String],
    RatecodeID:            Option[Long],
    PULocationID:          Long,
    DOLocationID:          Long,
    passenger_count:       Option[Int],
    trip_distance:         Double,
    fare_amount:           Double,
    extra:                 Double,
    mta_tax:               Double,
    tip_amount:            Double,
    tolls_amount:          Double,
    ehail_fee:             Option[Double],
    improvement_surcharge: Option[Double],
    total_amount:          Double,
    payment_type:          Option[Long],
    trip_type:             Option[Long],
    congestion_surcharge:  Option[Double]
  )

  case class FHVTripData(
    dispatching_base_num: String, //TLC Base License Number of the base that dispatched the trip
    pickup_datetime:      Instant, //Date and time of the trip pick-up
    dropOff_datetime:     Instant, //The date and time of the trip dropoff
    PUlocationID:         Option[Long], //TLC Taxi Zone in which the trip began
    DOlocationID:         Option[Long], //TLC Taxi Zone in which the trip ended
    //Indicates if the trip was a part of a shared ride chain offered by a
    //High Volume FHV company (e.g. Uber Pool, Lyft Line).
    //for shared trips, the value is 1. For non-shared rides, this field is null.
    SR_Flag:                Option[Int],
    Affiliated_base_number: String
  )

  case class PassengerCountByZoneAndTime(
    zoneId:         Long,
    pickupTS: Instant,
    passengerCount: Int
  )

  /** Данные
    *  features[x]->properties из файла ny_taxi_zones.geojson
    *
    * @param shape_area
    * @param objectid
    * @param shape_leng
    * @param location_id
    * @param zone
    * @param borough
    */
  case class NyTaxiZonesGeoJsonData(
    shape_area:  Double,
    objectid:    Int,
    shape_leng:  Double,
    location_id: Int,
    zone:        String,
    borough:     String
  )

  /** Данные из файла taxi_zone_lookup.csv
    *
    * some Examples:
    *  1,"EWR","Newark Airport","EWR"
    *  2,"Queens","Jamaica Bay","Boro Zone"
    *  3,"Bronx","Allerton/Pelham Gardens","Boro Zone"
    *  4,"Manhattan","Alphabet City","Yellow Zone"
    *  5,"Staten Island","Arden Heights","Boro Zone"
    */
  case class NyTaxiZonesLookup(
    locationID:   Int,
    borough:      String,
    zone:         String,
    service_zone: String
  )

}
