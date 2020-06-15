module.exports = {
    error_messages: {
        no_data_found: "No Data Found"
    },
    chartMapping : {
      "Device": "device",
      "Country": "organization_location_country",
      "City": "organization_location_city",
      "Department": "department_name",
      "subDepartment": "sub_department_name",
      "Source": "source_name",
      "ContentType":"content_type",
      "ResourceType":"resource_type",
      "Dealer":"dealer_group_code",
      "Region":"region",
      "GuildGroup":"Guild_Group_or Tier Classification",
      "Role":"job_title",
      "MimeType":"mime_type",
      "DeviceCategory":"device_category",
      "Zone":"zone",
      "Classification":"classification"

    },
    category: ["Technology", "Marketing and Sales", "Domain", "Process", "Behavioral and Business"],
    hours:[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23],
    non_filters_query_params: ["jl", "emailId", "puCode", "employeeId"],
    index_names: {
    },
}