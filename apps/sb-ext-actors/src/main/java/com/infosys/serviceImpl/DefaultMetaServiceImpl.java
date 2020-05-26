/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
package com.infosys.serviceImpl;

import com.infosys.model.*;
import com.infosys.service.DefaultMetaService;
import com.infosys.util.LexProjectUtil;
import org.springframework.stereotype.Service;
import org.sunbird.common.models.util.ProjectUtil;

import java.util.Date;
import java.util.Map;

@Service
public class DefaultMetaServiceImpl implements DefaultMetaService {


    public void setDefaultsInMetaStore(ContentMeta contentMeta) {
        contentMeta.setLoadingMessage("Loading...");
        contentMeta.setGrayScaleAppIcon("");
        contentMeta.setAppIcon("");
        contentMeta.setArtifactUrl("");
        contentMeta.setThumbnail("");
        contentMeta.setVisibility("Default");
        contentMeta.setPosterImage("");
        contentMeta.setLanguage(new String[]{"English"});
        contentMeta.setSourceName("");
        contentMeta.setSourceShortName("");
        contentMeta.setSourceUrl("url");
        contentMeta.setSourceIconUrl("");
        contentMeta.setExtractedTextForSearch("");
        contentMeta.setTranscript("");
        contentMeta.setLearningObjective("");
        contentMeta.setPreRequisites("");
        contentMeta.setInteractivityLevel("");
        contentMeta.setComplexityLevel("");
        contentMeta.setMinOsVersion(4.4);
        contentMeta.setChecksum("");
        contentMeta.setDownloadUrl("");
        contentMeta.setPkgVersion("1");
        contentMeta.setDeveloper("");
        contentMeta.setLicense("");
        contentMeta.setAttributions(new String[]{""});
        contentMeta.setCopyright(new String[]{""});
       // contentMeta.setIpReview(new ArrayList<String>());
        contentMeta.setIsExternal("no");
        contentMeta.setMe_averageInteractionsPerMin(new Double(0));
        contentMeta.setMe_averageRating(new Double(0));
        contentMeta.setMe_averageSessionsPerDevice(new Double(0));
        contentMeta.setMe_averageTimespentPerSession(new Double(0));
        contentMeta.setMe_totalComments(new Long(0));
        contentMeta.setMe_totalDevices(new Long(0));
        contentMeta.setMe_totalDownloads(new Long(0));
        contentMeta.setMe_totalInteractions(new Long(0));
        contentMeta.setMe_totalRatings(new Long(0));
        contentMeta.setMe_totalSessionsCount(new Long(0));
        contentMeta.setMe_totalSideloads(new Long(0));
        contentMeta.setMe_totalTimespent(new Long(0));
        contentMeta.setBody("");
        contentMeta.setVersionKey(new Date().getTime());
        contentMeta.setConcepts(new ObjectMeta[]{});
        contentMeta.setChildren(new ObjectMeta[]{});
        contentMeta.setCollections(new ObjectMeta[]{});
        contentMeta.setAccessibility(new String[]{});
        contentMeta.setMicrosites(new String[]{});
        contentMeta.setCollaborators(new String[]{});
        contentMeta.setCollaboratorDetails(new Details[]{});
        contentMeta.setVoiceCredits("");
        contentMeta.setImageCredits("");
        contentMeta.setSoundCredits("");
        contentMeta.setTranslatable(false);
        contentMeta.setForkable(false);
        contentMeta.setDomain("");
        contentMeta.setMsArtifactDetails(new Artifact());
        contentMeta.setTags(new Tag[]{});
        contentMeta.setSize(new Double(0));
        contentMeta.setPublisher("");
        contentMeta.setPublisherDetails(new Details[]{});
        contentMeta.setStatus("Draft");
        contentMeta.setTemplateType("");
        contentMeta.setSkills(new Skill[]{});
        contentMeta.setReleaseNotes("");
        contentMeta.setDescription("");
        contentMeta.setResourceType("");
        contentMeta.setIsIframeSupported("Yes");
        contentMeta.setMediaType(LexProjectUtil.MediaType.content.get());
    }


    public void setDefaultsForMapper(ContentMeta contentMeta) {
        contentMeta.setMe_averageInteractionsPerMin(new Double(0));
        contentMeta.setMe_averageRating(new Double(0));
        contentMeta.setMe_averageSessionsPerDevice(new Double(0));
        contentMeta.setMe_averageTimespentPerSession(new Double(0));
        contentMeta.setMe_totalComments(new Long(0));
        contentMeta.setMe_totalDevices(new Long(0));
        contentMeta.setMe_totalDownloads(new Long(0));
        contentMeta.setMe_totalInteractions(new Long(0));
        contentMeta.setMe_totalRatings(new Long(0));
        contentMeta.setMe_totalSessionsCount(new Long(0));
        contentMeta.setMe_totalSideloads(new Long(0));
        contentMeta.setMe_totalTimespent(new Long(0));
        contentMeta.setSize(new Double(0));
        contentMeta.setConcepts(new ObjectMeta[]{});
        contentMeta.setChildren(new ObjectMeta[]{});
        contentMeta.setCollections(new ObjectMeta[]{});
    }

    @Override
    public ContentMeta setDefaultToNullValues(ContentMeta contentMeta) {
        ContentMeta defaultObj = new ContentMeta();
        defaultObj.setIdentifier("");
        defaultObj.setName("");
        defaultObj.setDescription("");
        defaultObj.setKeywords(new String[]{});
        defaultObj.setLoadingMessage("Loading...");
        defaultObj.setAppIcon("");
        defaultObj.setGrayScaleAppIcon("");
        defaultObj.setThumbnail("");
        defaultObj.setMediaType(LexProjectUtil.MediaType.content.get());
        defaultObj.setContentType(LexProjectUtil.ContentType.resource.get());
        defaultObj.setVisibility(LexProjectUtil.Visibility.defaultVisibility.get());
        defaultObj.setPosterImage("");
        defaultObj.setLanguage(new String[]{"English"});
        defaultObj.setResourceType(LexProjectUtil.ResourceType.content.get());
        defaultObj.setMsArtifactDetails(null);
        defaultObj.setIdealScreenSize(LexProjectUtil.IdealScreenSize.seven.get());
        defaultObj.setSourceShortName("");
        defaultObj.setSourceName("");
        defaultObj.setSourceUrl("url");
        defaultObj.setSourceIconUrl("");
        defaultObj.setContentIdAtSource("");
        defaultObj.setContentUrlAtSource("");
        defaultObj.setExtractedTextForSearch("");
        defaultObj.setTranscript("");
        defaultObj.setUnit("");
        defaultObj.setTrack(new Track[]{});
        defaultObj.setTrackOwner("");
        defaultObj.setIsIframeSupported("Yes");
        defaultObj.setTrackContacts(new Details[]{});
        defaultObj.setTags(new Tag[]{});
        defaultObj.setIsExternal("No");
        defaultObj.setSkills(new Skill[]{});
        defaultObj.setLearningObjective("");
        defaultObj.setPreRequisites("");
        defaultObj.setInteractivityLevel("");
        defaultObj.setComplexityLevel("");
        defaultObj.setAudience(new String[]{});
        defaultObj.setDuration(0L);
        defaultObj.setSize(0.0);
        defaultObj.setMimeType("");
        defaultObj.setMinLexVersion("1.0");
        defaultObj.setMinOsVersion(4.4);
        defaultObj.setOs(new String[]{"All"});
        defaultObj.setChecksum("");
        defaultObj.setDownloadUrl("");
        defaultObj.setArtifactUrl("");
        defaultObj.setPkgVersion("");
        defaultObj.setDeveloper("");
        defaultObj.setLicense("");
        defaultObj.setAttributions(new String[]{});
        defaultObj.setCopyright(new String[]{"Infosys Limited"});
        defaultObj.setCreator("");
        defaultObj.setCreatorDetails(new CreatorDetails[]{});
        defaultObj.setPortalOwner("");
        defaultObj.setCreatorContacts(new Details[]{});
        defaultObj.setSubmitterDetails(null);
        //defaultObj.setIpReview(new ArrayList<String>() {
        //});

        defaultObj.setMe_averageInteractionsPerMin(0.0);
        defaultObj.setMe_totalSessionsCount(0L);
        defaultObj.setMe_totalTimespent(0L);                     
        defaultObj.setMe_averageTimespentPerSession(0.0);
        defaultObj.setMe_totalDevices(0L);
        defaultObj.setMe_totalInteractions(0L);
        defaultObj.setMe_averageSessionsPerDevice(0.0);
        defaultObj.setMe_totalSideloads(0L);
        defaultObj.setMe_totalComments(0L);
        defaultObj.setMe_totalRatings(0L);
        defaultObj.setMe_totalDownloads(0L);
        defaultObj.setMe_averageRating(0.0);

        defaultObj.setBody("");
        defaultObj.setPublisher("");
        defaultObj.setPublisherDetails(new Details[]{});
        defaultObj.setOwner("");
        defaultObj.setCollaborators(new String[]{});
        defaultObj.setCollaboratorDetails(new Details[]{});
        defaultObj.setVoiceCredits("");
        defaultObj.setSoundCredits("");
        defaultObj.setImageCredits("");
        defaultObj.setForkable(false);
        defaultObj.setTranslatable(false);
        defaultObj.setTemplateType("");
        defaultObj.setDomain("");
        defaultObj.setVersionCreatedBy("");
        defaultObj.setVersionDate(ProjectUtil.getFormattedDate().replace(' ', 'T'));
        defaultObj.setVersionKey(new Date().getTime());
        defaultObj.setLastUpdatedOn(ProjectUtil.getFormattedDate().replace(' ', 'T'));
        defaultObj.setLastUpdatedBy("");
        defaultObj.setStatus(LexProjectUtil.Status.DRAFT.getValue());
        defaultObj.setReleaseNotes("");
        defaultObj.setConcepts(new ObjectMeta[]{});
        defaultObj.setCollections(new ObjectMeta[]{});
        defaultObj.setChildren(new ObjectMeta[]{});
        defaultObj.setAccessibility(new String[]{"ALL"});
        defaultObj.setMicrosites(new String[]{"ALL"});

        Map<String, Object> defaultMap = defaultObj.toMap(false);
        Map<String, Object> contentMap = contentMeta.toMap(true);

        for (Map.Entry<String, Object> entry : contentMap.entrySet())
            if (entry.getValue() == null)
                contentMap.put(entry.getKey(), defaultMap.get(entry.getKey()));

        ContentMeta newContent = ContentMeta.fromMap(contentMap);
        return newContent;
    }

}
