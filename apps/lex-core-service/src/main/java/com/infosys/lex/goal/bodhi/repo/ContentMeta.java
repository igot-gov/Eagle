/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
///**
//© 2017 - 2019 Infosys Limited, Bangalore, India. All Rights Reserved. 
//Version: 1.10
//
//Except for any free or open source software components embedded in this Infosys proprietary software program (“Program”),
//this Program is protected by copyright laws, international treaties and other pending or existing intellectual property rights in India,
//the United States and other countries. Except as expressly permitted, any unauthorized reproduction, storage, transmission in any form or
//by any means (including without limitation electronic, mechanical, printing, photocopying, recording or otherwise), or any distribution of 
//this Program, or any portion of it, may result in severe civil and criminal penalties, and will be prosecuted to the maximum extent possible
//under the law.
//
//Highly Confidential
// 
//*/
package com.infosys.lex.goal.bodhi.repo;

//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentMeta {
//
//	// General
	private String identifier;
	private String name;
//	private String description;
//
//	private String[] keywords;
//	private String loadingMessage;
//
//	private String appIcon;
//	private String thumbnail;
//	private String posterImage;
//	private String grayScaleAppIcon;
//
//	private String mediaType;
//	private String contentType;
//	private String visibility;
//
//	private String[] language;
//
//	private String resourceType;
//	private Artifact msArtifactDetails;
//	private String idealScreenSize;
//	// General extended meta
//	private String sourceShortName;
//	private String sourceName;
//	private String sourceUrl;
//	private String sourceIconUrl;
//
//	private String contentIdAtSource;
//	private String contentUrlAtSource;
//	private String extractedTextForSearch; // change to extractedText
//	private String transcript;
//	private String unit;
//	private Track[] track;
//	private String trackOwner;
//	private String isIframeSupported;
//	private Details[] trackContacts;
//	private Tag[] tags;
//	private String isExternal;
//	// Pedagogy
//	private Skill[] skills;
//	private String learningObjective;
//	private String preRequisites;
//	private String interactivityLevel;
//	private String complexityLevel; // Enum: Beginner, Intermediate and Advanced
//	private String[] audience;
//	// Technical
//	private Long duration;
//	private Double size;
//	private String mimeType;
//	private String minLexVersion;
//	private Double minOsVersion;
//	private String[] os;
//	private String checksum;
//	private String downloadUrl;
//	private String artifactUrl;
//	private String pkgVersion;
//	// Ownership
//	private String developer;
//	private String license;
//	private String[] attributions;
//	private String[] copyright;
//	private String creator; // As per response author
//	private CreatorDetails[] creatorDetails;
//	private String portalOwner;
//	private Details[] creatorContacts;
//	private Details submitterDetails;
//	// private List<String> ipReview;
//	// Analytics
//	private Double me_averageInteractionsPerMin;
//	private Long me_totalSessionsCount;
//	private Long me_totalTimespent;
//	private Double me_averageTimespentPerSession;
//	private Long me_totalDevices;
//	private Long me_totalInteractions;
//	private Double me_averageSessionsPerDevice;
//	private Long me_totalSideloads;
//	private Long me_totalComments;
//	private Long me_totalRatings;
//	private Long me_totalDownloads;
//	private Double me_averageRating;
//	// Authoring
//	private String body;
//	private String publisher;
//	private Details[] publisherDetails;
//	private String owner;
//	private String[] collaborators;
//	private Details[] collaboratorsDetails;
//	private String voiceCredits;
//	private String soundCredits;
//	private String imageCredits;
//	private Boolean forkable;
//	private Boolean translatable;
//	private String templateType;
//	private String domain;
//	// Lifecycle
//	private String versionCreatedBy;
//	private String versionDate;
//	private Number versionKey; // As per response versionKey
//	private String lastUpdatedOn;
//	private String lastUpdatedBy;
//	private String status;
//	private String releaseNotes;
//	private String certificationUrl;
//	// Relations
//	private ObjectMeta[] concepts;
//	private ObjectMeta[] collections;
//	private ObjectMeta[] children;
//	private ContentMeta[] certificationList;
//	// Access restriction
//	private String[] accessibility;
//	private String[] microsites;
//	private String courseType;
//	// only Authoring tool
//	private List<Map<String, Object>> comments;
//	private String stageIcons;
//	private String editorState;
//	private String hasAssessment;
//	private Boolean isRejected;
//	private String[] resourceCategory;
//	private Client[] clients;
//
//	// new Attributes FotR Authering Tools
//	private ContentMetaHead[] preContents;
//	private ContentMetaHead[] postContents;
//	private String[] systemRequirements;
//	private SoftwareRequirement[] softwareRequirements;
//	private String etaTrack;
//	private References[] references;
//
//	// for certifications
//	private Double passPercentage;
//	private String certificationStatus;
//	private String nextCertificationAttemptDate;
//	private Double recentCerticationAttemptScore;
//	private String certificationSubmissionDate;
//
//	public String getNextCertificationAttemptDate() {
//		return nextCertificationAttemptDate;
//	}
//
//	public void setNextCertificationAttemptDate(String nextCertificationAttemptDate) {
//		this.nextCertificationAttemptDate = nextCertificationAttemptDate;
//	}
//
//	public String getCertificationSubmissionDate() {
//		return certificationSubmissionDate;
//	}
//
//	public void setCertificationSubmissionDate(String certificationSubmissionDate) {
//		this.certificationSubmissionDate = certificationSubmissionDate;
//	}
//
//	public Double getRecentCerticationAttemptScore() {
//		return recentCerticationAttemptScore;
//	}
//
//	public void setRecentCerticationAttemptScore(Double recentCerticationAttemptScore) {
//		this.recentCerticationAttemptScore = recentCerticationAttemptScore;
//	}
//
//	public String getCertificationStatus() {
//		return certificationStatus;
//	}
//
//	public void setCertificationStatus(String certificationStatus) {
//		this.certificationStatus = certificationStatus;
//	}
//
//	public Double getPassPercentage() {
//		return passPercentage;
//	}
//
//	public void setPassPercentage(Double passPercentage) {
//		this.passPercentage = passPercentage;
//	}
//
//	public static ContentMeta fromMap(Map<String, Object> map) {
//		return new ObjectMapper().convertValue(map, ContentMeta.class);
//	}
//
//	public static ContentMeta fromJson(String json) throws IOException {
//		return new ObjectMapper().readValue(json, ContentMeta.class);
//	}
//
//	public String getCertificationUrl() {
//		return certificationUrl;
//	}
//
//	public void setCertificationUrl(String certificationUrl) {
//		this.certificationUrl = certificationUrl;
//	}
//
//	public ContentMeta[] getCertificationList() {
//		return certificationList;
//	}
//
//	public void setCertificationList(ContentMeta[] certificationList) {
//		this.certificationList = certificationList;
//	}
//
//	public String getCourseType() {
//		return courseType;
//	}
//
//	public void setCourseType(String courseType) {
//		this.courseType = courseType;
//	}
//
//	public void setIsRejected(Boolean isRejected) {
//		this.isRejected = isRejected;
//	}
//
//	public Client[] getClients() {
//		return clients;
//	}
//
//	public void setClients(Client[] clients) {
//		this.clients = clients;
//	}
//
//	public String[] getResourceCategory() {
//		return resourceCategory;
//	}
//
//	public void setResourceCategory(String[] resourceCategory) {
//		this.resourceCategory = resourceCategory;
//	}
//
//	public Boolean getIsRejected() {
//		return isRejected;
//	}
//
//	public void setIsRejected(boolean isRejected) {
//		this.isRejected = isRejected;
//	}
//
//	public String getHasAssessment() {
//		return hasAssessment;
//	}
//
//	public void setHasAssessment(String hasAssessment) {
//		this.hasAssessment = hasAssessment;
//	}
//
//	public List<Map<String, Object>> getComments() {
//		return comments;
//	}
//
//	public void setComments(List<Map<String, Object>> comments) {
//		this.comments = comments;
//	}
//
//	public String getIdealScreenSize() {
//		return idealScreenSize;
//	}
//
//	public void setIdealScreenSize(String idealScreenSize) {
//		this.idealScreenSize = idealScreenSize;
//	}
//
//	@SuppressWarnings("unchecked")
//	public Map<String, Object> toMap(boolean keepNulls) {
//		ObjectMapper mapper = new ObjectMapper();
//		if (!keepNulls) {
//			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		}
//		return mapper.convertValue(this, Map.class);
//	}
//
//	public String toJson() {
//		try {
//			return new ObjectMapper().writeValueAsString(this);
//		} catch (JsonProcessingException ex) {
//			return "";
//		}
//	}
//
//	public String getContentUrlAtSource() {
//		return contentUrlAtSource;
//	}
//
//	public void setContentUrlAtSource(String contentUrlAtSource) {
//		this.contentUrlAtSource = contentUrlAtSource;
//	}
//
//	public String getMinLexVersion() {
//		return minLexVersion;
//	}
//
//	public void setMinLexVersion(String minLexVersion) {
//		this.minLexVersion = minLexVersion;
//	}
//
//	public boolean isForkable() {
//		return forkable;
//	}
//
//	public boolean isTranslatable() {
//		return translatable;
//	}
//
//	public String getIsIframeSupported() {
//		return isIframeSupported;
//	}
//
//	public void setIsIframeSupported(String isIframeSupported) {
//		this.isIframeSupported = isIframeSupported;
//	}
//
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
//
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public String[] getKeywords() {
//		return keywords;
//	}
//
//	public void setKeywords(String[] keywords) {
//		this.keywords = keywords;
//	}
//
//	public String getLoadingMessage() {
//		return loadingMessage;
//	}
//
//	public void setLoadingMessage(String loadingMessage) {
//		this.loadingMessage = loadingMessage;
//	}
//
//	public String getAppIcon() {
//		return appIcon;
//	}
//
//	public void setAppIcon(String appIcon) {
//		this.appIcon = appIcon;
//	}
//
//	public String getGrayScaleAppIcon() {
//		return grayScaleAppIcon;
//	}
//
//	public void setGrayScaleAppIcon(String grayScaleAppIcon) {
//		this.grayScaleAppIcon = grayScaleAppIcon;
//	}
//
//	public String getThumbnail() {
//		return thumbnail;
//	}
//
//	public void setThumbnail(String thumbnail) {
//		this.thumbnail = thumbnail;
//	}
//
//	public String getMediaType() {
//		return mediaType;
//	}
//
//	public void setMediaType(String mediaType) {
//		this.mediaType = mediaType;
//	}
//
//	public String getContentType() {
//		return contentType;
//	}
//
//	public void setContentType(String contentType) {
//		this.contentType = contentType;
//	}
//
//	public String getVisibility() {
//		return visibility;
//	}
//
//	public void setVisibility(String visibility) {
//		this.visibility = visibility;
//	}
//
//	public String getPosterImage() {
//		return posterImage;
//	}
//
//	public void setPosterImage(String posterImage) {
//		this.posterImage = posterImage;
//	}
//
//	public String[] getLanguage() {
//		return language;
//	}
//
//	public void setLanguage(String[] language) {
//		this.language = language;
//	}
//
//	public String getResourceType() {
//		return resourceType;
//	}
//
//	public void setResourceType(String resourceTye) {
//		this.resourceType = resourceTye;
//	}
//
//	public String getSourceShortName() {
//		return sourceShortName;
//	}
//
//	public void setSourceShortName(String sourceShortName) {
//		this.sourceShortName = sourceShortName;
//	}
//
//	public String getSourceName() {
//		return sourceName;
//	}
//
//	public void setSourceName(String sourceName) {
//		this.sourceName = sourceName;
//	}
//
//	public String getSourceUrl() {
//		return sourceUrl;
//	}
//
//	public void setSourceUrl(String sourceUrl) {
//		this.sourceUrl = sourceUrl;
//	}
//
//	public String getSourceIconUrl() {
//		return sourceIconUrl;
//	}
//
//	public void setSourceIconUrl(String sourceIconUrl) {
//		this.sourceIconUrl = sourceIconUrl;
//	}
//
//	public String getContentIdAtSource() {
//		return contentIdAtSource;
//	}
//
//	public void setContentIdAtSource(String contentIdAtSource) {
//		this.contentIdAtSource = contentIdAtSource;
//	}
//
//	public String getExtractedTextForSearch() {
//		return extractedTextForSearch;
//	}
//
//	public void setExtractedTextForSearch(String extractedTextForSearch) {
//		this.extractedTextForSearch = extractedTextForSearch;
//	}
//
//	public String getTranscript() {
//		return transcript;
//	}
//
//	public void setTranscript(String transcript) {
//		this.transcript = transcript;
//	}
//
//	public String getUnit() {
//		return unit;
//	}
//
//	public void setUnit(String unit) {
//		this.unit = unit;
//	}
//
//	public String getStageIcons() {
//		return stageIcons;
//	}
//
//	public void setStageIcons(String stageIcons) {
//		this.stageIcons = stageIcons;
//	}
//
//	public String getEditorState() {
//		return editorState;
//	}
//
//	public void setEditorState(String editorState) {
//		this.editorState = editorState;
//	}
//
//	public Track[] getTrack() {
//		return track;
//	}
//
//	public void setTrack(Track[] track) {
//		this.track = track;
//	}
//
//	public String getTrackOwner() {
//		return trackOwner;
//	}
//
//	public void setTrackOwner(String trackOwner) {
//		this.trackOwner = trackOwner;
//	}
//
//	public Details[] getTrackContacts() {
//		return trackContacts;
//	}
//
//	public void setTrackContacts(Details[] trackContacts) {
//		this.trackContacts = trackContacts;
//	}
//
//	public Skill[] getSkills() {
//		return skills;
//	}
//
//	public void setSkills(Skill[] skills) {
//		this.skills = skills;
//	}
//
//	public String getLearningObjective() {
//		return learningObjective;
//	}
//
//	public void setLearningObjective(String learningObjective) {
//		this.learningObjective = learningObjective;
//	}
//
//	public String getPreRequisites() {
//		return preRequisites;
//	}
//
//	public void setPreRequisites(String preRequisites) {
//		this.preRequisites = preRequisites;
//	}
//
//	public String getInteractivityLevel() {
//		return interactivityLevel;
//	}
//
//	public void setInteractivityLevel(String interactivityLevel) {
//		this.interactivityLevel = interactivityLevel;
//	}
//
//	public String getComplexityLevel() {
//	return complexityLevel;
//}
//
//public void setComplexityLevel(String complexityLevel) {
//	this.complexityLevel = complexityLevel;
//}
//
//	public String[] getAudience() {
//		return audience;
//	}
//
//	public void setAudience(String[] audience) {
//		this.audience = audience;
//	}
//
//	public Long getDuration() {
//		return duration;
//	}
//
//	public void setDuration(Long duration) {
//		this.duration = duration;
//	}
//
//	public Double getSize() {
//		return size;
//	}
//
//	public void setSize(Double size) {
//		this.size = size;
//	}
//
//	public String getMimeType() {
//		return mimeType;
//	}
//
//	public void setMimeType(String mimeType) {
//		this.mimeType = mimeType;
//	}
//
//	public Double getMinOsVersion() {
//		return minOsVersion;
//	}
//
//	public void setMinOsVersion(Double minOsVersion) {
//		this.minOsVersion = minOsVersion;
//	}
//
//	public String[] getOs() {
//		return os;
//	}
//
//	public void setOs(String[] os) {
//		this.os = os;
//	}
//
//	public String getChecksum() {
//		return checksum;
//	}
//
//	public void setChecksum(String checksum) {
//		this.checksum = checksum;
//	}
//
//	public String getDownloadUrl() {
//		return downloadUrl;
//	}
//
//	public void setDownloadUrl(String downloadUrl) {
//		this.downloadUrl = downloadUrl;
//	}
//
//	public String getArtifactUrl() {
//		return artifactUrl;
//	}
//
//	public void setArtifactUrl(String artifactUrl) {
//		this.artifactUrl = artifactUrl;
//	}
//
//	public String getPkgVersion() {
//		return pkgVersion;
//	}
//
//	public void setPkgVersion(String pkgVersion) {
//		this.pkgVersion = pkgVersion;
//	}
//
//	public String getDeveloper() {
//		return developer;
//	}
//
//	public void setDeveloper(String developer) {
//		this.developer = developer;
//	}
//
//	public String getLicense() {
//		return license;
//	}
//
//	public void setLicense(String license) {
//		this.license = license;
//	}
//
//	public String[] getAttributions() {
//		return attributions;
//	}
//
//	public void setAttributions(String[] attributions) {
//		this.attributions = attributions;
//	}
//
//	public String[] getCopyright() {
//		return copyright;
//	}
//
//	public void setCopyright(String[] copyright) {
//		this.copyright = copyright;
//	}
//
//	public String getCreator() {
//		return creator;
//	}
//
//	public void setCreator(String creator) {
//		this.creator = creator;
//	}
//
//	public CreatorDetails[] getCreatorDetails() {
//		return creatorDetails;
//	}
//
//	public void setCreatorDetails(CreatorDetails[] creatorDetails) {
//		this.creatorDetails = creatorDetails;
//	}
//
//	public String getPortalOwner() {
//		return portalOwner;
//	}
//
//	public void setPortalOwner(String portalOwner) {
//		this.portalOwner = portalOwner;
//	}
//
//	public Details[] getCreatorContacts() {
//		return creatorContacts;
//	}
//
//	public void setCreatorContacts(Details[] creatorContacts) {
//		this.creatorContacts = creatorContacts;
//	}
//
//	public Details getSubmitterDetails() {
//		return submitterDetails;
//	}
//
//	public void setSubmitterDetails(Details submitterDetails) {
//		this.submitterDetails = submitterDetails;
//	}
//
//	/*
//	 * public List<String> getIpReview() { return ipReview; }
//	 * 
//	 * public void setIpReview(List<String> ipReview) { this.ipReview = ipReview; }
//	 */
//
//	public Double getMe_averageInteractionsPerMin() {
//		return me_averageInteractionsPerMin;
//	}
//
//	public void setMe_averageInteractionsPerMin(Double me_averageInteractionsPerMin) {
//		this.me_averageInteractionsPerMin = me_averageInteractionsPerMin;
//	}
//
//	public Long getMe_totalSessionsCount() {
//		return me_totalSessionsCount;
//	}
//
//	public void setMe_totalSessionsCount(Long me_totalSessionsCount) {
//		this.me_totalSessionsCount = me_totalSessionsCount;
//	}
//
//	public Long getMe_totalTimespent() {
//		return me_totalTimespent;
//	}
//
//	public void setMe_totalTimespent(Long me_totalTimespent) {
//		this.me_totalTimespent = me_totalTimespent;
//	}
//
//	public Double getMe_averageTimespentPerSession() {
//		return me_averageTimespentPerSession;
//	}
//
//	public void setMe_averageTimespentPerSession(Double me_averageTimespentPerSession) {
//		this.me_averageTimespentPerSession = me_averageTimespentPerSession;
//	}
//
//	public Long getMe_totalDevices() {
//		return me_totalDevices;
//	}
//
//	public void setMe_totalDevices(Long me_totalDevices) {
//		this.me_totalDevices = me_totalDevices;
//	}
//
//	public Long getMe_totalInteractions() {
//		return me_totalInteractions;
//	}
//
//	public void setMe_totalInteractions(Long me_totalInteractions) {
//		this.me_totalInteractions = me_totalInteractions;
//	}
//
//	public Double getMe_averageSessionsPerDevice() {
//		return me_averageSessionsPerDevice;
//	}
//
//	public void setMe_averageSessionsPerDevice(Double me_averageSessionsPerDevice) {
//		this.me_averageSessionsPerDevice = me_averageSessionsPerDevice;
//	}
//
//	public Long getMe_totalSideloads() {
//		return me_totalSideloads;
//	}
//
//	public void setMe_totalSideloads(Long me_totalSideloads) {
//		this.me_totalSideloads = me_totalSideloads;
//	}
//
//	public Long getMe_totalComments() {
//		return me_totalComments;
//	}
//
//	public void setMe_totalComments(Long me_totalComments) {
//		this.me_totalComments = me_totalComments;
//	}
//
//	public Long getMe_totalRatings() {
//		return me_totalRatings;
//	}
//
//	public void setMe_totalRatings(Long me_totalRatings) {
//		this.me_totalRatings = me_totalRatings;
//	}
//
//	public Long getMe_totalDownloads() {
//		return me_totalDownloads;
//	}
//
//	public void setMe_totalDownloads(Long me_totalDownloads) {
//		this.me_totalDownloads = me_totalDownloads;
//	}
//
//	public Double getMe_averageRating() {
//		return me_averageRating;
//	}
//
//	public void setMe_averageRating(Double me_averageRating) {
//		this.me_averageRating = me_averageRating;
//	}
//
//	public String getBody() {
//		return body;
//	}
//
//	public void setBody(String body) {
//		this.body = body;
//	}
//
//	public String getPublisher() {
//		return publisher;
//	}
//
//	public void setPublisher(String publisher) {
//		this.publisher = publisher;
//	}
//
//	public Details[] getPublisherDetails() {
//		return publisherDetails;
//	}
//
//	public void setPublisherDetails(Details[] publisherDetails) {
//		this.publisherDetails = publisherDetails;
//	}
//
//	public String getOwner() {
//		return owner;
//	}
//
//	public void setOwner(String owner) {
//		this.owner = owner;
//	}
//
//	public Details[] getCollaboratorsDetails() {
//		return collaboratorsDetails;
//	}
//
//	/*
//	 * public String[] getCollaborators() { return collaborators; }
//	 * 
//	 * public void setCollaborators(String[] collaborators) { this.collaborators =
//	 * collaborators; }
//	 */
//
//	public void setCollaboratorsDetails(Details[] collaboratorsDetails) {
//		this.collaboratorsDetails = collaboratorsDetails;
//	}
//
//	public void setCollaboratorDetails(Details[] collaboratorsDetails) {
//		this.collaboratorsDetails = collaboratorsDetails;
//	}
//
//	public String getVoiceCredits() {
//		return voiceCredits;
//	}
//
//	public void setVoiceCredits(String voiceCredits) {
//		this.voiceCredits = voiceCredits;
//	}
//
//	public String getSoundCredits() {
//		return soundCredits;
//	}
//
//	public void setSoundCredits(String soundCredits) {
//		this.soundCredits = soundCredits;
//	}
//
//	public String getImageCredits() {
//		return imageCredits;
//	}
//
//	public void setImageCredits(String imageCredits) {
//		this.imageCredits = imageCredits;
//	}
//
//	public Boolean getForkable() {
//		return forkable;
//	}
//
//	public void setForkable(Boolean forkable) {
//		this.forkable = forkable;
//	}
//
//	public Boolean getTranslatable() {
//		return translatable;
//	}
//
//	public void setTranslatable(Boolean translatable) {
//		this.translatable = translatable;
//	}
//
//	public String getTemplateType() {
//		return templateType;
//	}
//
//	public void setTemplateType(String templateType) {
//		this.templateType = templateType;
//	}
//
//	public String getDomain() {
//		return domain;
//	}
//
//	public void setDomain(String domain) {
//		this.domain = domain;
//	}
//
//	public String getVersionCreatedBy() {
//		return versionCreatedBy;
//	}
//
//	public void setVersionCreatedBy(String versionCreatedBy) {
//		this.versionCreatedBy = versionCreatedBy;
//	}
//
//	public String getVersionDate() {
//		return versionDate;
//	}
//
//	public void setVersionDate(String versionDate) {
//		this.versionDate = versionDate;
//	}
//
//	public Number getVersionKey() {
//		return versionKey;
//	}
//
//	public void setVersionKey(Number versionKey) {
//		this.versionKey = versionKey;
//	}
//
//	public String getLastUpdatedOn() {
//		return lastUpdatedOn;
//	}
//
//	public void setLastUpdatedOn(String lastUpdatedOn) {
//		this.lastUpdatedOn = lastUpdatedOn;
//	}
//
//	public String getLastUpdatedBy() {
//		return lastUpdatedBy;
//	}
//
//	public void setLastUpdatedBy(String lastUpdatedBy) {
//		this.lastUpdatedBy = lastUpdatedBy;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getReleaseNotes() {
//		return releaseNotes;
//	}
//
//	public void setReleaseNotes(String releaseNotes) {
//		this.releaseNotes = releaseNotes;
//	}
//
//	public ObjectMeta[] getConcepts() {
//		return concepts;
//	}
//
//	public void setConcepts(ObjectMeta[] concepts) {
//		this.concepts = concepts;
//	}
//
//	public ObjectMeta[] getCollections() {
//		return collections;
//	}
//
//	public void setCollections(ObjectMeta[] collections) {
//		this.collections = collections;
//	}
//
//	public ObjectMeta[] getChildren() {
//		return children;
//	}
//
//	public void setChildren(ObjectMeta[] children) {
//		this.children = children;
//	}
//
//	public String[] getAccessibility() {
//		return accessibility;
//	}
//
//	public void setAccessibility(String[] accessibility) {
//		this.accessibility = accessibility;
//	}
//
//	public String[] getMicrosites() {
//		return microsites;
//	}
//
//	public void setMicrosites(String[] microsites) {
//		this.microsites = microsites;
//	}
//
//	public String[] getCollaborators() {
//		return collaborators;
//	}
//
//	public void setCollaborators(String[] collaborators) {
//		this.collaborators = collaborators;
//	}
//
//	public Tag[] getTags() {
//		return tags;
//	}
//
//	public void setTags(Tag[] tags) {
//		this.tags = tags;
//	}
//
//	public String getIsExternal() {
//		return isExternal;
//	}
//
//	public void setIsExternal(String isExternal) {
//		this.isExternal = isExternal;
//	}
//
//	public Artifact getMsArtifactDetails() {
//		return msArtifactDetails;
//	}
//
//	public void setMsArtifactDetails(Artifact msArtifactDetails) {
//		this.msArtifactDetails = msArtifactDetails;
//	}
//
//	public ContentMetaHead[] getPreContents() {
//		return preContents;
//	}
//
//	public void setPreContents(ContentMetaHead[] preContents) {
//		this.preContents = preContents;
//	}
//
//	public ContentMetaHead[] getPostContents() {
//		return postContents;
//	}
//
//	public void setPostContents(ContentMetaHead[] postContents) {
//		this.postContents = postContents;
//	}
//
//	public String[] getSystemRequirements() {
//		return systemRequirements;
//	}
//
//	public void setSystemRequirements(String[] systemRequirements) {
//		this.systemRequirements = systemRequirements;
//	}
//
//	public SoftwareRequirement[] getSoftwareRequirements() {
//		return softwareRequirements;
//	}
//
//	public void setSoftwareRequirements(SoftwareRequirement[] softwareRequirements) {
//		this.softwareRequirements = softwareRequirements;
//	}
//
//	public String getEtaTrack() {
//		return etaTrack;
//	}
//
//	public void setEtaTrack(String etaTrack) {
//		this.etaTrack = etaTrack;
//	}
//
//	public References[] getReferences() {
//		return references;
//	}
//
//	public void setReferences(References[] references) {
//		this.references = references;
//	}
//
}
