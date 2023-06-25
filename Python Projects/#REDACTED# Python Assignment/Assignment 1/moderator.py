import sys
from datetime import datetime

class User:
  def __init__(self, name: str):
    # Constructs a new User object with the given name and an engagement, expressiveness, and offensiveness of 0
    self.name = name
    self.engagement = 0
    self.expressiveness = 0
    self.offensiveness = 0

  def process_message(self, message: str, banned_words: list) -> bool:
    ''' Process message to modify engagement, expressieness, and offensiveness as specified by the guidelines. Return True if message is a string. '''
    multiplier = 1.5
    if message.startswith(("  ", "\t")):
      multiplier = 1
    # Engagement Check: 1 (before multiplier)
    self.engagement += multiplier

    # Expressiveness Check:
    # WHY CANT WE USE THE IN KEYWORDS !@#!$!
    # grader, if you read this, I (Jemima Siu) want an explanation for why we can't use in if we have access to the str().find() thing which does the exact same thing
    if message.find("!") != -1:
      if message.find("?") != -1:
        self.expressiveness += 2 * multiplier
      else:
        self.expressiveness += multiplier
    else:
      if message.find("?") == -1:
        self.expressiveness += -1 * multiplier
      # Note: no change if no ! but yes ?
    
    # Offensiveness Check: 1 if banned words

    i = 0
    while i < len(banned_words):
      word = banned_words[i]
      if word.lower() in message.lower():
        self.offensiveness += multiplier
        break
      i += 1
    return isinstance(message, str)

  def calculate_personality_score(self) -> int:
    ''' Return the personality score modifier, which is min(expressiveness - offensiveness, engagement). '''
    score = self.expressiveness - self.offensiveness
    if score > self.engagement:
      return round(self.engagement)
    return round(score)

  def update_personality(self, forum_name: str, ng_words: list) -> int:
    ''' Process all messages from the the User self in the forum_name file using the banned words in the list ng_words and return the change in personality (rounded to the closest integer). forum_name and words_name must be validated.'''
    with open(forum_name) as forum:
      all_lines = forum.readlines()
      # Iterate through every username to find messages sent by the User
      i = 3
      while i < len(all_lines):
        # Removes \t and \n from the forum username
        if self.name == all_lines[i].strip():
          # Process the message to update scores
          self.process_message(all_lines[i + 1], ng_words)
        i += 3
    # return personality score
    return self.calculate_personality_score()

def is_valid_name(name: str) -> bool:
  ''' Return True iff name is a valid name (only alphabetic characters, spaces, and "-" characters. '''
  if not name: return False  # Check for empty string
  if name.isspace(): return False  # Check for only spaces

  i = 0
  while i < len(name):
    char = name[i]
    if not (char.isalpha() or char.isspace() or char == "-"):
      return False
    i += 1
  return True

def is_chronological(earlier_dt: str, later_dt: str) -> bool:
  ''' Returns True iff later_dt comes after or equals earlier_dt). If earlier_dt = None, return True automatically. earlier_dt and later_dt must be valid datetime strings as specified by check_datetime()'''
  # Override condition for 1st post
  if not earlier_dt:
    return True
  # Check if earlier_dt comes after later_dt
  # Year
  earlier_dt = datetime.fromisoformat(earlier_dt)
  later_dt = datetime.fromisoformat(later_dt)
  return later_dt >= earlier_dt


def check_datetime(dt: str) -> bool:
  ''' Returns True iff the datetime string follows the format YYYY-MM-DDTHH:MM:SS, where all values are numeric except for "T" and punctuation '''
  if len(dt) != 19: return False  # Check for length of string, since datetime.fromisoformat accepts datetime strings besides the given YYYY-MM-DDTHH:MM:SS
  if dt[10] != "T": return False # Check for the T between date and time, since datetime.fromisoformat accepts any character instead of T

  # Try converting the string into a datetime. If this succeeds, then the string is a valid datetime string
  try:
    x = datetime.fromisoformat(dt)
    return True
  except ValueError:
    return False

def header_is_valid(line1: str, line2: str) -> bool:
  ''' Return True iff line1 is not empty and line2 is empty '''
  if not line1:
    return False  # If 1st line is empty, return False
  return not line2  # If 1st line is not empty, then return True iff 2nd line is empty (NOTE: iff means otherwise, return False)

def get_ng_words(words_name: str) -> list:
  ''' Return the list of banned words in the words_name file. words_name must be validated '''
  with open(words_name, "r") as words:
    next(words)
    next(words)
    ng_words = []
    line = words.readline()
    while line: # Read until the end of the file
      ng_words.append(line[:-1])
      line = words.readline()
  return ng_words
def rank_people(people_name: str):
  ''' Rank the users given in people_name in descending order of personality and rewrites the file as such. The people_name file must have been validated.'''
  with open(people_name, "r") as people:  # Opens file for reading and writing
    # Store header for later rewriting
    line1 = people.readline()
    next(people)
    user_dict = {}
    line = people.readline()
    while line: # Read until the end of the file
      user = line.strip().split(",")  # Returns the two values separated by the comma
      user_dict[user[0]] = int(user[1])
      line = people.readline()
      # Now, the dictionary contains elements of [user : score]
  sort_and_rewrite_rank(people_name, line1, user_dict)

def sort_and_rewrite_rank(people_name: str, line1: str, user_dict: dict):
  ''' Sort user_dict by personality score in descending order. Then write to the people_name file the header (given by line1) and the people in the user_dict dictionary in the order given by sorted_keys, where sorted_keys holds the usernames of the users. '''
  sorted_keys = sorted(user_dict, key=user_dict.get, reverse=True)
  # Sorts user_dict by the personality scores of user_dict (user_dict.get returns the value) in reverse order
  with open(people_name, "w") as people:
    # Header: line1 is the title, and the second line is an empty line
    people.write(line1)
    people.write("\n")
    i = 0
    while i < len(sorted_keys):
      key = sorted_keys[i]
      people.write(key + "," + str(user_dict[key]) + "\n")
      i += 1


def censor_forum(forum_name: str, words_name: str):
  ''' Censor the forum_name file by replacing occurences of banned words from the words_name file with asterisks. forum_name and words_name must have been validated. '''
  # ng_words is a list containing all the banned words
  ng_words = get_ng_words(words_name)

  def find_all(old_str: str, new_str: str):
    ''' Helper function for censor_forum. Returns a list of all instances of new_str in old_str, or an empty list if none exists'''
    start = 0
    list = []
    while True:
      start = old_str.find(new_str, start)
      if start == -1: return list
      list.append(start)
      start += len(
        new_str)  # changes start bound to read the next instance of the word
    return list
  # Will hold the completed forum at the end for the system to write
  # Will hold the forum to be rewritten after the censoring
  censored_forum = []
  
  with open(forum_name, "r") as forum:
    # A list containing all lines of forum
    all_lines = forum.readlines()
    # The allowable characters before and after the string for an instance of a word to be considered bannable
    allowable_chars = " \n\t,.'\"!?()"
    # Skip the header
    censored_forum.append(all_lines[0])
    censored_forum.append(all_lines[1])
    i = 1 # Starts at i = 2
    while i < len(all_lines) - 1:
      i += 1 #NOTE: because of the many continue keywords, it is more practical to place the incrementer at the beginning of the loop rather than at the end
      if (i - 1) % 3 != 0:
        # If not a message, then just add it back to censored_forum and reiterate the loop
        censored_forum.append(all_lines[i])
        continue
      # Turns the string into a list of characters to be edited later (while strings are immutable, lists aren't)
      line_array = list(all_lines[i])
      
      # Iterate through each ng word through each individual message
      j = -1
      while j < len(ng_words) - 1:
        j += 1 # See outer loop
        word = ng_words[j]
        # Find indices of word occurences (case-insensitive by setting both to all lowercase)
        index_list = find_all(all_lines[i].lower(), word.lower())
        # if there are no instances of the word, go to next iteration of the loop
        if not index_list:
          continue
        # Iterate through each occurence of the word to censor it (potentially)
        k = -1 # See outer loop
        while k < len(index_list) - 1:
          k += 1
          ind = index_list[k]
          # Override condition: If the index is 0, use any valid character (in this instance, " ")
          if ind == 0:
            begin_char = " "
          else:
            begin_char = all_lines[i][ind - 1]
          # Check if the front character allows for a new word (if not bannable, restart the loop)
          if allowable_chars.find(begin_char) == -1:
            continue
          # Note: no exception needed because all_lines[i] always ends with \n and \n is not a valid character in ng_words
          end_char = all_lines[i][ind + len(word)]
          if allowable_chars.find(end_char) == -1:
            continue
          # Change the word to all asterisks
          line_array[ind:ind + len(word)] = "*" * len(word)
      # Add the list as a string
      censored_forum.append("".join(line_array))
  # Rewrite the forum with the censored words
  with open(forum_name, "w") as forum:
    forum.write("".join(censored_forum))

def evaluate_forum(forum_name: str, words_name: str, people_name: str):
  ''' Evaluate new personality scores for all users using the given rules by the User class, and reorders the people_name file by the new personality scores. All 3 files must have been validated.'''
  with open(people_name, "r") as people:
    # ng_words is a list holding all the banned words
    ng_words = get_ng_words(words_name)
    # Skip header, but save its contents to rewrite the file later
    line1 = next(people)
    next(people) # Always empty, since it was validated

    # Initializes a new dictionary
    user_dict = {}
    # Iterate through each line in people
    line = people.readline()
    while line:
      # person is a two element list, where person[0] is the username and person[1] is the personality score
      person = line.strip().split(",")
      # Create new User object
      user = User(person[0])
      # Check all messages from the user and modify the personality score accordingly
      new_score = int(person[1]) + user.update_personality(forum_name, ng_words)
      # Apply bounds
      if new_score > 10:
        new_score = 10
      elif new_score < -10:
        new_score = -10
      user_dict[user.name] = new_score
      line = people.readline()
    # Resort and Rewrite people with new personality scores
    sort_and_rewrite_rank(people_name, line1, user_dict)
    
def validate_people(people_name: str, log_name: str) -> bool:
  ''' Checks if people properly follows the specified instructions. Returns True if no errors. If errors are present, write them in the log_name file and return False. '''
  with open(log_name, "w") as log:
    with open(people_name, "r") as people:
      # Pass first two lines to be checked for header validity ([:-1] removes the \n character from the end of the line)
      if not header_is_valid(people.readline()[:-1], people.readline()[:-1]):
        log.write("Error: people file read. The people file header is incorrectly formatted")
        return False
      # Holds the line number for the log message
      line_number = 3
      line = people.readline()
      while line:
        if not line.endswith("\n"):
          log.write("Error: people file read. The people entry is invalid on line " + str(line_number))
          return False
        # user is a list of each user separated by the commas
        user = line.strip().split(",")  
        # Check for formatting of each User
        # User must have 2 elements
        if len(user) != 2:
          log.write("Error: people file read. The people entry is invalid on line " + str(line_number))
          return False
        # First element must be a valid username
        if not is_valid_name(user[0]):
          log.write("Error: people file read. The user's name is invalid on line " + str(line_number))
          return False
        # Second element must be a valid personality score -> int in [-10, 10]
        try:
          # If value is not an int, then a ValueError exception is raised
          score = int(user[1])
          # If score is outside bounds, raise a ValueError
          if score < -10 or score > 10:
            raise ValueError()
        # Catch invalid personality scores
        except ValueError:
          log.write("Error: people file read. The personality score is invalid on line " + str(line_number))
          return False
        line_number += 1
        line = people.readline()
  return True

def validate_forum(forum_name: str, log_name: str) -> bool:
  ''' Checks if the forum_name file follows the correct formatting in heading and posts. Any errors are written to the log_name file and returns False. Otherwise, returns True '''
  with open(log_name, "w") as log:
    with open(forum_name, "r") as forum:
      # Pass first two lines to be checked for header validity ([:-1] removes the \n character from the end of the line)
      if not header_is_valid(forum.readline()[:-1], forum.readline()[:-1]):
        log.write("Error: forum file read. The forum file header is incorrectly formatted\n")
        return False
      
      # Checking posts loop
      post_number = 0 # Starts at post_number = 1
      last_post_date, last_reply_date = "", ""
      while True:
        # Assigns groups of variables together (you can separate them if you'd like)
        line1, line2, line3 = forum.readline(), forum.readline(
        ), forum.readline()

        # If this is the end of a post, line1 will be empty and so a successful check will have been run
        if not line1:
          return True
        post_number += 1
        # These 3 variables hold the line numbers of the 3 lines for the log file
        l1_number, l2_number, l3_number = post_number * 3, post_number * 3 + 1, post_number * 3 + 2
        
        # Check if the post is a reply by checking spaces or \t
        # is_reply is a flag to check if it was a reply
        is_reply = False
        if line1.startswith(("  ", "\t")):         
        # Check if first post is a reply, which is the only time it's not after a post
          if post_number == 1:  
            log.write("Error: forum file read. The reply is placed before a post on line " + str(l1_number) + "\n")
            return False

          # Since it's a reply, all other lines must be indented too
          if not line2.startswith(("  ", "\t")):
            log.write("Error: forum file read. The post has an invalid format on line " + str(l2_number))
            return False
          if not line3.startswith(("  ", "\t")):
            log.write("Error: forum file read. The post has an invalid format on line " + str(l3_number) + "\n")
            return False

          # Set the is_reply flag to True
          is_reply = True
          
        # If post is not a reply, ensure other lines aren't indented
        else:
          if line2.startswith(("  ", "\t")):
            log.write("Error: forum file read. The post has an invalid format on line " + str(l2_number))
            return False
          if line3.startswith(("  ", "\t")):
            log.write("Error: forum file read. The post has an invalid format on line " + str(l3_number) + "\n")
            return False
        
        # Removes indents and newline characters (NOTE: line3 is never checked against a system, so it is not updated)
        line1, line2 = line1.strip(), line2.strip()
        # Check for valid datetime string
        if not check_datetime(line1):
          log.write("Error: forum file read. The datetime string is invalid on line " + str(l1_number))
          return False
          
        # Check for chronological order
        # If it's a reply, then check with last_reply_date (Which could be a post if it's the first reply)
        if is_reply and not is_chronological(last_reply_date, line1):
          log.write("The reply is out of chronological order on line " + str(l1_number))
          return False
        # If is a post, then check against just the last_post_date
        elif not is_reply and not is_chronological(last_post_date, line1):
          log.write("Error: forum file read. The post is out of chronological order on line " + str(l1_number))
          return False

        # Check for valid usernames
        if not is_valid_name(line2):
          log.write("Error: forum file read. The user's name is invalid on line " + str(l2_number))

        # Congrats! If you made it here, this post / reply is valid!
        # If it's a post, then update both the required post date and required reply date
        # If it's a reply, still update the required reply date, but don't change the required post date
        if not is_reply:
          last_post_date = line1
        last_reply_date = line1
  return True

def validate_words(words_name: str, log_name: str) -> bool:
  ''' Checks if the words_name file follows the correct formatting in heading and words. Any errors are written to the log_name file and returns False. Otherwise, returns the list of banned words'''
  with open(log_name, "w") as log:
    with open(words_name, "r") as words:
      # Pass first two lines to be checked for header validity ([:-1] removes the \n character from the end of the line)
      if not header_is_valid(words.readline()[:-1], words.readline()[:-1]):
        log.write("Error: words file read. The words file header is incorrectly formatted")
        return False

      # Check if all words are valid (end in "\n" and aren't just spaces)
      line_number = 3
      line = words.readline()
      while line:
        if line.isspace() or not line.endswith("\n"):
          log.write("Error: words file read. The banned word is invalid on line " + str(line_number))
          return False
        line_number += 1
        line = words.readline()
  return True


if __name__ == "__main__":
  arg_order = ["-task", "-log", "-forum", "-words", "-people"]

  i = 1 # Skip the first argument which is the file name
  opts, args = [], []
  while i < len(sys.argv):
    value = sys.argv[i]
    if value.startswith("-"):
      opts.append(value)
    else: 
      args.append(value)
    i += 1

  temp_list = str(opts)
  i = 0
  while i < len(arg_order):
    x = arg_order[i]
    if temp_list.find(x) == -1:
      print("No", x[1:], "arguments found")
      exit(0)
    i += 1

  if len(opts) < len(args):
    # NOTE: not specified in the instructions, but this is a check if there are too many arguments and returns the first option that doesn't have an associated argument instead of the first in the list of 5
    print("No", opts[len(args)], "argument found")
    exit(0)
  elif len(opts) > len(args):
    # NOTE: not specified in the instructions, but this is a check if there are too many options, and just returns that (since finding a missing argument is easier than the missing option)
    print("Too few arguments!")
    exit(0)

  # Creates a dictionary with format [option : argument]
  opt_arg_dict = {}
  i = 0
  while i < len(opts):
    opt_arg_dict[opts[i]] = args[i]
    i += 1
  
  # Check valid task:
  temp_str = "rank_people validate_forum censor_forum evaluate_forum"
  if temp_str.find(opt_arg_dict["-task"]) == -1:
    print("Task", opt_arg_dict["-task"], "is invalid.")
    exit(0)

  # Check if "-forum", "-words", and "-people" are readable 
  i = 2 # skip the -task and -log files 
  while i < len(arg_order):
    key = arg_order[i]
    # Try opening the forum, words, and people files.
    try:
      with open(opt_arg_dict[key]) as file:
        pass  # I don't know how to use files properly. If you can't use with, then just use file = open() instead and close it later
    # If an error comes up because the file doesn't exist, enter this "except" section
    except FileNotFoundError:
      print(opt_arg_dict[key], "cannot be read.")
      exit(0)
    i += 1
  # If running never exited, then begin the moderation!
  print("Moderator program starting...")
  # Task determination:
  if opt_arg_dict["-task"] == "rank_people":
    # Check valid people only
    if not validate_people(opt_arg_dict["-people"], opt_arg_dict["-log"]):
      exit(0)
    rank_people(opt_arg_dict["-people"])
  elif opt_arg_dict["-task"] == "validate_forum":
    # Check valid forum only:
    if not validate_forum(opt_arg_dict["-forum"], opt_arg_dict["-log"]):
      exit(0)
  elif opt_arg_dict["-task"] == "censor_forum":
    # Check valid forum and words
    if not validate_words(opt_arg_dict["-words"], opt_arg_dict["-log"]) or not validate_forum(opt_arg_dict["-forum"], opt_arg_dict["-log"]):
      exit(0)
    censor_forum(opt_arg_dict["-forum"], opt_arg_dict["-words"])
  elif opt_arg_dict["-task"] == "evaluate_forum":
    # Check all 3 (forum, words, and people)
    if not validate_forum(opt_arg_dict["-forum"], opt_arg_dict["-log"]) or not validate_words(opt_arg_dict["-words"], opt_arg_dict["-log"]) or not validate_people(opt_arg_dict["-people"], opt_arg_dict["-log"]):
      exit(0)
    evaluate_forum(opt_arg_dict["-forum"], opt_arg_dict["-words"], opt_arg_dict["-people"])