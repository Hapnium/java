//package com.hapnium.core;
//
//import com.serch.server.bases.ApiResponse;
//import com.serch.server.core.location.responses.Address;
//import com.serch.server.core.location.services.implementations.LocationImplementation;
//import com.serch.server.domains.shop.responses.SearchShopResponse;
//
//import java.util.List;
//
///**
// * Interface for location-related services.
// * <p>
// * This interface provides methods for retrieving location predictions and
// * searching for places based on their unique identifiers. Implementations
// * of this interface should interact with the underlying mapping or location
// * services to provide accurate and timely results for user queries.
// * </p>
// *
// * @see LocationImplementation
// */
//interface LocationService {
//    /**
//     * Retrieves predictions for a given query.
//     * <p>
//     * This method accepts a user-provided query string and returns a list
//     * of potential address matches. It is commonly used for auto-complete
//     * functionality, allowing users to receive suggestions as they type.
//     * The results are encapsulated within an {@link ApiResponse}, which
//     * can also include metadata about the request status.
//     * </p>
//     *
//     * @param query The query to search for predictions. This is typically a
//     *              partial address or location name that the user is typing.
//     * @return A list of {@link Address}
//     *         objects that match the query. If no predictions are found,
//     *         the list may be empty, and the response should indicate the
//     *         status of the request.
//     */
//    List<Address> predictions(String query);
//
//    /**
//     * Searches for nearby shops based on the specified category and location.
//     *
//     * @param keyword the keyword to search for (e.g., "restaurant", "cafe")
//     * @param category the category of shops to search for (e.g., "restaurant", "cafe")
//     * @param longitude the longitude of the location to search around
//     * @param latitude the latitude of the location to search around
//     * @param radius the radius (in meters) within which to search for nearby shops
//     * @return a list of {@link SearchShopResponse} objects representing the nearby shops
//     *         that match the specified category within the given radius
//     */
//    List<SearchShopResponse> nearbySearch(String keyword, String category, Double longitude, Double latitude, Double radius);
//}