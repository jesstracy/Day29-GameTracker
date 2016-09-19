package com.tiy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessicatracy on 9/15/16.
 */
@Controller
public class GameTrackerController {

    @Autowired
    GameRepository games;

    @Autowired
    UserRepository users;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, String userName, String password) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            user = new User(userName, password);
            users.save(user);
        } else if (!password.equals(user.getPassword())) {
            throw new Exception("Incorrect password");
        }
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // First one just to show the form
//    @RequestMapping(path = "/", method = RequestMethod.GET)
//    public String home() {
//        return "home";
//    }

    // For when we want to add games to our model.
//    @RequestMapping(path = "/", method = RequestMethod.GET)
//    public String home(Model model) {
//        Iterable<Game> allGames = games.findAll();
//        List<Game> gameList = new ArrayList<Game>();
//        for (Game game : allGames) {
//            gameList.add(game);
//        }
//        model.addAttribute("games", gameList);
//        return "home";
//    }

    //For when we are querying by genre and year
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String home(Model model, HttpSession session, String genre, Integer releaseYear) {

        // added this if statement after added login method.
        // if there is a user, add it to the model so the view can see it
        if (session.getAttribute("user") != null) {
            model.addAttribute("user", session.getAttribute("user"));
        }

        List<Game> gameList = new ArrayList<Game>();
        if (genre != null) {
            gameList = games.findByGenre(genre);
        } else if (releaseYear != null) {
            gameList = games.findByReleaseYear(releaseYear);
        } else {
            User savedUser = (User)session.getAttribute("user");
            if (savedUser != null) {
                gameList = games.findByUser(savedUser);
            } else {
                Iterable<Game> allGames = games.findAll();
                for (Game game : allGames) {
                    gameList.add(game);
                }
            }
        }
        model.addAttribute("games", gameList);
        return "home";
    }

    @RequestMapping(path = "/searchByName", method = RequestMethod.GET)
    public String queryGamesByName(Model model, String search) {
        System.out.println("Searching by ..." + search);
        List<Game> gameList = games.findByNameStartsWith(search);
        model.addAttribute("games", gameList);
        return "home";
    }

    @RequestMapping(path = "/add-game", method = RequestMethod.POST)
    public String addGame(HttpSession session, String gameName, String gamePlatform, String gameGenre, int gameYear) {
        User user = (User) session.getAttribute("user");
        Game game = new Game(gameName, gamePlatform, gameGenre, gameYear, user);
        System.out.println("My runtime repo: " + games.toString());
        games.save(game);
        return "redirect:/";
    }

    @RequestMapping(path = "/delete", method = RequestMethod.GET)
    public String deleteGame(Model model, Integer gameID) {
        if (gameID != null) {
            //get delete method free from crud, just like add
            games.delete(gameID);
        }

        return "redirect:/";
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public String games(Model model, HttpSession session) {
        return "games";
    }
}
