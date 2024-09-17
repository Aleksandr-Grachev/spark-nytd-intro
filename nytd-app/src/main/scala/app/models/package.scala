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
  case class YellowTripData(
    Airport_fee:           Double,
    congestion_surcharge:  Double,
    DOLocationID:          Long,
    extra:                 Double,
    fare_amount:           Double,
    improvement_surcharge: Double,
    mta_tax:               Double,
    passenger_count:       Long,
    payment_type:          Long,
    PULocationID:          Long,
    RatecodeID:            Long,
    // Rec:                   Option[Long], TODO: ???
    store_and_fwd_flag:    String,
    tip_amount:            Double,
    tolls_amount:          Double,
    total_amount:          Double,
    tpep_dropoff_datetime: Instant,
    tpep_pickup_datetime:  Instant,
    trip_distance:         Double,
    VendorID:              Long
  )

}
