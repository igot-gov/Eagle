struct Preference {
    static var defaultInstance = Preference()

    //RTMP channel endpoints
    var uri: String? = ""
    var streamName: String? = "instance1"
}
