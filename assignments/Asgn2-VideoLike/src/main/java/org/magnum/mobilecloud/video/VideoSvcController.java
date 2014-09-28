package org.magnum.mobilecloud.video;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

/**
 * This simple VideoSvc allows clients to send HTTP POST requests with videos
 * that are stored in memory using a list. Clients can send HTTP GET requests to
 * receive a JSON listing of the videos that have been sent to the controller so
 * far. Stopping the controller will cause it to lose the history of videos that
 * have been sent to it because they are stored in memory.
 * 
 * Notice how much simpler this VideoSvc is than the original VideoServlet?
 * Spring allows us to dramatically simplify our service. Another important
 * aspect of this version is that we have defined a VideoSvcApi that provides
 * strong typing on both the client and service interface to ensure that we
 * don't send the wrong paraemters, etc.
 * 
 * @author jules
 *
 */

// Tell Spring that this class is a Controller that should
// handle certain HTTP requests for the DispatcherServlet
@Controller
public class VideoSvcController {

	// The VideoRepository that we are going to store our videos
	// in. We don't explicitly construct a VideoRepository, but
	// instead mark this object as a dependency that needs to be
	// injected by Spring. Our Application class has a method
	// annotated with @Bean that determines what object will end
	// up being injected into this member variable.
	//
	// Also notice that we don't even need a setter for Spring to
	// do the injection.
	//
	@Autowired
	private VideoRepository videos;

	// Receives POST requests to /video and converts the HTTP
	// request body, which should contain json, into a Video
	// object before adding it to the list. The @RequestBody
	// annotation on the Video parameter is what tells Spring
	// to interpret the HTTP request body as JSON and convert
	// it into a Video object to pass into the method. The
	// @ResponseBody annotation tells Spring to conver the
	// return value from the method back into JSON and put
	// it into the body of the HTTP response to the client.
	//
	// The VIDEO_SVC_PATH is set to "/video" in the VideoSvcApi
	// interface. We use this constant to ensure that the
	// client and service paths for the VideoSvc are always
	// in synch.
	//
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v) {
		return videos.save(v);
	}

	// Receives GET requests to /video and returns the current
	// list of videos in memory. Spring automatically converts
	// the list of videos to JSON because of the @ResponseBody
	// annotation.
	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> getVideoList() {
		return Lists.newArrayList(videos.findAll());
	}

	// Receives GET requests to /video/find and returns all Videos
	// that have a title (e.g., Video.name) matching the "title" request
	// parameter value that is passed by the client
	@RequestMapping(value = VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByTitle(
	// Tell Spring to use the "title" parameter in the HTTP request's query
	// string as the value for the title method parameter
			@RequestParam(VideoSvcApi.TITLE_PARAMETER) String title) {
		return videos.findByName(title);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method = RequestMethod.GET)
	public @ResponseBody Collection<Video> findByDurationLessThan(
	// Tell Spring to use the "title" parameter in the HTTP request's query
	// string as the value for the title method parameter
			@RequestParam(VideoSvcApi.DURATION_PARAMETER) Long duration) {
		return videos.findByDurationLessThan(duration);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method = RequestMethod.POST)
	public @ResponseBody Video likeVideo(@PathVariable("id") Long id,
			Principal p) throws VideoServiceException {
		Video v = videos.findOne(id);
		Set<String> usersLikeVideo = null;
		if (v == null) {
			throw new VideoNotFoundException("Missing video with id " + id);
		}
		if (v.getUsersLikeIt() == null) {
			usersLikeVideo = new HashSet<String>();
		} else {
			if (v.getUsersLikeIt().contains(p.getName())) {
				throw new VideoServiceException(
						"You cannot like the same video twice ");
			}
			usersLikeVideo = v.getUsersLikeIt();
		}
		v.setLikes(v.getLikes() + 1);
		usersLikeVideo.add(p.getName());
		v.setUsersLikeIt(usersLikeVideo);
		return videos.save(v);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method = RequestMethod.POST)
	public @ResponseBody Video unlikeVideo(@PathVariable("id") Long id, Principal p) throws VideoServiceException {
		Video v = videos.findOne(id);
		Set<String> usersLikeVideo = null;
		if (v == null) {
			throw new VideoNotFoundException("Missing video with id " + id);
		}
		if (v.getUsersLikeIt() == null
				|| !v.getUsersLikeIt().contains(p.getName())) {
			throw new VideoServiceException(
					"You cannot unlike the video with id " + id);
		}
		usersLikeVideo = v.getUsersLikeIt();
		v.setLikes(v.getLikes() - 1);
		usersLikeVideo.remove(p.getName());
		v.setUsersLikeIt(usersLikeVideo);
		return videos.save(v);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method = RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") Long id) {
		return videos.findOne(id);
	}

	@RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method = RequestMethod.GET)
	public @ResponseBody Set<String> getUsersWhoLikedVideo(@PathVariable("id") Long id) throws VideoNotFoundException {
		Video v = videos.findOne(id);
		if (v == null) {
			throw new VideoNotFoundException("Missing video with id " + id);
		}
		return v.getUsersLikeIt();
	}

}
