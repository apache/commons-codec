/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.codec.language;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.StringEncoderAbstractTest;

/**
 * Tests {@link DoubleMetaphone}.
 * 
 * @see "http://www.cuj.com/documents/s=8038/cuj0006philips/"
 * @author Apache Software Foundation
 * @version $Id$
 */
public class DoubleMetaphoneTest extends StringEncoderAbstractTest {

    /**
     * Test data from http://aspell.sourceforge.net/test/batch0.tab.
     * 
     * "Copyright (C) 2002 Kevin Atkinson (kevina@gnu.org). Verbatim copying
     * and distribution of this entire article is permitted in any medium,
     * provided this notice is preserved."
     * 
     * Massaged the test data in the array below.
     */
    private static final String[][] FIXTURE = { { "Accosinly", "Occasionally" }, {
            "Ciculer", "Circler" }, {
            "Circue", "Circle" }, {
            "Maddness", "Madness" }, {
            "Occusionaly", "Occasionally" }, {
            "Steffen", "Stephen" }, {
            "Thw", "The" }, {
            "Unformanlly", "Unfortunately" }, {
            "Unfortally", "Unfortunately" }, {
            "abilitey", "ability" }, {
            "abouy", "about" }, {
            "absorbtion", "absorption" }, {
            "accidently", "accidentally" }, {
            "accomodate", "accommodate" }, {
            "acommadate", "accommodate" }, {
            "acord", "accord" }, {
            "adultry", "adultery" }, {
            "aggresive", "aggressive" }, {
            "alchohol", "alcohol" }, {
            "alchoholic", "alcoholic" }, {
            "allieve", "alive" }, {
            "alot", "a lot" }, {
            "alright", "all right" }, {
            "amature", "amateur" }, {
            "ambivilant", "ambivalent" }, {
            "amification", "amplification" }, {
            "amourfous", "amorphous" }, {
            "annoint", "anoint" }, {
            "annonsment", "announcement" }, {
            "annoyting", "anting" }, {
            "annuncio", "announce" }, {
            "anonomy", "anatomy" }, {
            "anotomy", "anatomy" }, {
            "antidesestablishmentarianism", "antidisestablishmentarianism" }, {
            "antidisestablishmentarism", "antidisestablishmentarianism" }, {
            "anynomous", "anonymous" }, {
            "appelet", "applet" }, {
            "appreceiated", "appreciated" }, {
            "appresteate", "appreciate" }, {
            "aquantance", "acquaintance" }, {
            "aratictature", "architecture" }, {
            "archeype", "archetype" }, {
            "aricticure", "architecture" }, {
            "artic", "arctic" }, {
            "asentote", "asymptote" }, {
            "ast", "at" }, {
            "asterick", "asterisk" }, {
            "asymetric", "asymmetric" }, {
            "atentively", "attentively" }, {
            "autoamlly", "automatically" }, {
            "bankrot", "bankrupt" }, {
            "basicly", "basically" }, {
            "batallion", "battalion" }, {
            "bbrose", "browse" }, {
            "beauro", "bureau" }, {
            "beaurocracy", "bureaucracy" }, {
            "beggining", "beginning" }, {
            "beging", "beginning" }, {
            "behaviour", "behavior" }, {
            "beleive", "believe" }, {
            "belive", "believe" }, {
            "benidifs", "benefits" }, {
            "bigginging", "beginning" }, {
            "blait", "bleat" }, {
            "bouyant", "buoyant" }, {
            "boygot", "boycott" }, {
            "brocolli", "broccoli" }, {
            "buch", "bush" }, {
            "buder", "butter" }, {
            "budr", "butter" }, {
            "budter", "butter" }, {
            "buracracy", "bureaucracy" }, {
            "burracracy", "bureaucracy" }, {
            "buton", "button" }, {
            "byby", "by by" }, {
            "cauler", "caller" }, {
            "ceasar", "caesar" }, {
            "cemetary", "cemetery" }, {
            "changeing", "changing" }, {
            "cheet", "cheat" }, {
            "cicle", "circle" }, {
            "cimplicity", "simplicity" }, {
            "circumstaces", "circumstances" }, {
            "clob", "club" }, {
            "coaln", "colon" }, {
            "cocamena", "cockamamie" }, {
            "colleaque", "colleague" }, {
            "colloquilism", "colloquialism" }, {
            "columne", "column" }, {
            "comiler", "compiler" }, {
            "comitmment", "commitment" }, {
            "comitte", "committee" }, {
            "comittmen", "commitment" }, {
            "comittmend", "commitment" }, {
            "commerciasl", "commercials" }, {
            "commited", "committed" }, {
            "commitee", "committee" }, {
            "companys", "companies" }, {
            "compicated", "complicated" }, {
            "comupter", "computer" }, {
            "concensus", "consensus" }, {
            "confusionism", "confucianism" }, {
            "congradulations", "congratulations" }, {
            "conibation", "contribution" }, {
            "consident", "consistent" }, {
            "consident", "consonant" }, {
            "contast", "constant" }, {
            "contastant", "constant" }, {
            "contunie", "continue" }, {
            "cooly", "coolly" }, {
            "copping", "coping" }, {
            "cosmoplyton", "cosmopolitan" }, {
            "courst", "court" }, {
            "crasy", "crazy" }, {
            "cravets", "caveats" }, {
            "credetability", "credibility" }, {
            "criqitue", "critique" }, {
            "croke", "croak" }, {
            "crucifiction", "crucifixion" }, {
            "crusifed", "crucified" }, {
            "ctitique", "critique" }, {
            "cumba", "combo" }, {
            "custamisation", "customization" }, {
            "dag", "dog" }, {
            "daly", "daily" }, {
            "danguages", "dangerous" }, {
            "deaft", "draft" }, {
            "defence", "defense" }, {
            "defenly", "defiantly" }, {
            "definate", "definite" }, {
            "definately", "definitely" }, {
            "dependeble", "dependable" }, {
            "descrption", "description" }, {
            "descrptn", "description" }, {
            "desparate", "desperate" }, {
            "dessicate", "desiccate" }, {
            "destint", "distant" }, {
            "develepment", "developments" }, {
            "developement", "development" }, {
            "develpond", "development" }, {
            "devulge", "divulge" }, {
            "diagree", "disagree" }, {
            "dieties", "deities" }, {
            "dinasaur", "dinosaur" }, {
            "dinasour", "dinosaur" }, {
            "direcyly", "directly" }, {
            "discuess", "discuss" }, {
            "disect", "dissect" }, {
            "disippate", "dissipate" }, {
            "disition", "decision" }, {
            "dispair", "despair" }, {
            "disssicion", "discussion" }, {
            "distarct", "distract" }, {
            "distart", "distort" }, {
            "distroy", "destroy" }, {
            "documtations", "documentation" }, {
            "doenload", "download" }, {
            "dongle", "dangle" }, {
            "doog", "dog" }, {
            "dramaticly", "dramatically" }, {
            "drunkeness", "drunkenness" }, {
            "ductioneery", "dictionary" }, {
            "dur", "due" }, {
            "duren", "during" }, {
            "dymatic", "dynamic" }, {
            "dynaic", "dynamic" }, {
            "ecstacy", "ecstasy" }, {
            "efficat", "efficient" }, {
            "efficity", "efficacy" }, {
            "effots", "efforts" }, {
            "egsistence", "existence" }, {
            "eitiology", "etiology" }, {
            "elagent", "elegant" }, {
            "elligit", "elegant" }, {
            "embarass", "embarrass" }, {
            "embarassment", "embarrassment" }, {
            "embaress", "embarrass" }, {
            "encapsualtion", "encapsulation" }, {
            "encyclapidia", "encyclopedia" }, {
            "encyclopia", "encyclopedia" }, {
            "engins", "engine" }, {
            "enhence", "enhance" }, {
            "enligtment", "Enlightenment" }, {
            "ennuui", "ennui" }, {
            "enought", "enough" }, {
            "enventions", "inventions" }, {
            "envireminakl", "environmental" }, {
            "enviroment", "environment" }, {
            "epitomy", "epitome" }, {
            "equire", "acquire" }, {
            "errara", "error" }, {
            "erro", "error" }, {
            "evaualtion", "evaluation" }, {
            "evething", "everything" }, {
            "evtually", "eventually" }, {
            "excede", "exceed" }, {
            "excercise", "exercise" }, {
            "excpt", "except" }, {
            "excution", "execution" }, {
            "exhileration", "exhilaration" }, {
            "existance", "existence" }, {
            "expleyly", "explicitly" }, {
            "explity", "explicitly" }, {
            "expresso", "espresso" }, {
            "exspidient", "expedient" }, {
            "extions", "extensions" }, {
            "factontion", "factorization" }, {
            "failer", "failure" }, {
            "famdasy", "fantasy" }, {
            "faver", "favor" }, {
            "faxe", "fax" }, {
            "febuary", "february" }, {
            "firey", "fiery" }, {
            "fistival", "festival" }, {
            "flatterring", "flattering" }, {
            "fluk", "flux" }, {
            "flukse", "flux" }, {
            "fone", "phone" }, {
            "forsee", "foresee" }, {
            "frustartaion", "frustrating" }, {
            "fuction", "function" }, {
            "funetik", "phonetic" }, {
            "futs", "guts" }, {
            "gamne", "came" }, {
            "gaurd", "guard" }, {
            "generly", "generally" }, {
            "ghandi", "gandhi" }, {
            "goberment", "government" }, {
            "gobernement", "government" }, {
            "gobernment", "government" }, {
            "gotton", "gotten" }, {
            "gracefull", "graceful" }, {
            "gradualy", "gradually" }, {
            "grammer", "grammar" }, {
            "hallo", "hello" }, {
            "hapily", "happily" }, {
            "harrass", "harass" }, {
            "havne", "have" }, {
            "heellp", "help" }, {
            "heighth", "height" }, {
            "hellp", "help" }, {
            "helo", "hello" }, {
            "herlo", "hello" }, {
            "hifin", "hyphen" }, {
            "hifine", "hyphen" }, {
            "higer", "higher" }, {
            "hiphine", "hyphen" }, {
            "hippie", "hippy" }, {
            "hippopotamous", "hippopotamus" }, {
            "hlp", "help" }, {
            "hourse", "horse" }, {
            "houssing", "housing" }, {
            "howaver", "however" }, {
            "howver", "however" }, {
            "humaniti", "humanity" }, {
            "hyfin", "hyphen" }, {
            "hypotathes", "hypothesis" }, {
            "hypotathese", "hypothesis" }, {
            "hystrical", "hysterical" }, {
            "ident", "indent" }, {
            "illegitament", "illegitimate" }, {
            "imbed", "embed" }, {
            "imediaetly", "immediately" }, {
            "imfamy", "infamy" }, {
            "immenant", "immanent" }, {
            "implemtes", "implements" }, {
            "inadvertant", "inadvertent" }, {
            "incase", "in case" }, {
            "incedious", "insidious" }, {
            "incompleet", "incomplete" }, {
            "incomplot", "incomplete" }, {
            "inconvenant", "inconvenient" }, {
            "inconvience", "inconvenience" }, {
            "independant", "independent" }, {
            "independenent", "independent" }, {
            "indepnends", "independent" }, {
            "indepth", "in depth" }, {
            "indispensible", "indispensable" }, {
            "inefficite", "inefficient" }, {
            "inerface", "interface" }, {
            "infact", "in fact" }, {
            "influencial", "influential" }, {
            "inital", "initial" }, {
            "initinized", "initialized" }, {
            "initized", "initialized" }, {
            "innoculate", "inoculate" }, {
            "insistant", "insistent" }, {
            "insistenet", "insistent" }, {
            "instulation", "installation" }, {
            "intealignt", "intelligent" }, {
            "intejilent", "intelligent" }, {
            "intelegent", "intelligent" }, {
            "intelegnent", "intelligent" }, {
            "intelejent", "intelligent" }, {
            "inteligent", "intelligent" }, {
            "intelignt", "intelligent" }, {
            "intellagant", "intelligent" }, {
            "intellegent", "intelligent" }, {
            "intellegint", "intelligent" }, {
            "intellgnt", "intelligent" }, {
            "intensionality", "intensionally" }, {
            "interate", "iterate" }, {
            "internation", "international" }, {
            "interpretate", "interpret" }, {
            "interpretter", "interpreter" }, {
            "intertes", "interested" }, {
            "intertesd", "interested" }, {
            "invermeantial", "environmental" }, {
            "irregardless", "regardless" }, {
            "irresistable", "irresistible" }, {
            "irritible", "irritable" }, {
            "islams", "muslims" }, {
            "isotrop", "isotope" }, {
            "isreal", "israel" }, {
            "johhn", "john" }, {
            "judgement", "judgment" }, {
            "kippur", "kipper" }, {
            "knawing", "knowing" }, {
            "latext", "latest" }, {
            "leasve", "leave" }, {
            "lesure", "leisure" }, {
            "liasion", "lesion" }, {
            "liason", "liaison" }, {
            "libary", "library" }, {
            "likly", "likely" }, {
            "lilometer", "kilometer" }, {
            "liquify", "liquefy" }, {
            "lloyer", "layer" }, {
            "lossing", "losing" }, {
            "luser", "laser" }, {
            "maintanence", "maintenance" }, {
            "majaerly", "majority" }, {
            "majoraly", "majority" }, {
            "maks", "masks" }, {
            "mandelbrot", "Mandelbrot" }, {
            "mant", "want" }, {
            "marshall", "marshal" }, {
            "maxium", "maximum" }, {
            "meory", "memory" }, {
            "metter", "better" }, {
            "mic", "mike" }, {
            "midia", "media" }, {
            "millenium", "millennium" }, {
            "miniscule", "minuscule" }, {
            "minkay", "monkey" }, {
            "minum", "minimum" }, {
            "mischievious", "mischievous" }, {
            "misilous", "miscellaneous" }, {
            "momento", "memento" }, {
            "monkay", "monkey" }, {
            "mosaik", "mosaic" }, {
            "mostlikely", "most likely" }, {
            "mousr", "mouser" }, {
            "mroe", "more" }, {
            "neccessary", "necessary" }, {
            "necesary", "necessary" }, {
            "necesser", "necessary" }, {
            "neice", "niece" }, {
            "neighbour", "neighbor" }, {
            "nemonic", "pneumonic" }, {
            "nevade", "Nevada" }, {
            "nickleodeon", "nickelodeon" }, {
            "nieve", "naive" }, {
            "noone", "no one" }, {
            "noticably", "noticeably" }, {
            "notin", "not in" }, {
            "nozled", "nuzzled" }, {
            "objectsion", "objects" }, {
            "obsfuscate", "obfuscate" }, {
            "ocassion", "occasion" }, {
            "occuppied", "occupied" }, {
            "occurence", "occurrence" }, {
            "octagenarian", "octogenarian" }, {
            "olf", "old" }, {
            "opposim", "opossum" }, {
            "organise", "organize" }, {
            "organiz", "organize" }, {
            "orientate", "orient" }, {
            "oscilascope", "oscilloscope" }, {
            "oving", "moving" }, {
            "paramers", "parameters" }, {
            "parametic", "parameter" }, {
            "paranets", "parameters" }, {
            "partrucal", "particular" }, {
            "pataphysical", "metaphysical" }, {
            "patten", "pattern" }, {
            "permissable", "permissible" }, {
            "permition", "permission" }, {
            "permmasivie", "permissive" }, {
            "perogative", "prerogative" }, {
            "persue", "pursue" }, {
            "phantasia", "fantasia" }, {
            "phenominal", "phenomenal" }, {
            "picaresque", "picturesque" }, {
            "playwrite", "playwright" }, {
            "poeses", "poesies" }, {
            "polation", "politician" }, {
            "poligamy", "polygamy" }, {
            "politict", "politic" }, {
            "pollice", "police" }, {
            "polypropalene", "polypropylene" }, {
            "pompom", "pompon" }, {
            "possable", "possible" }, {
            "practicle", "practical" }, {
            "pragmaticism", "pragmatism" }, {
            "preceeding", "preceding" }, {
            "precion", "precision" }, {
            "precios", "precision" }, {
            "preemptory", "peremptory" }, {
            "prefices", "prefixes" }, {
            "prefixt", "prefixed" }, {
            "presbyterian", "Presbyterian" }, {
            "presue", "pursue" }, {
            "presued", "pursued" }, {
            "privielage", "privilege" }, {
            "priviledge", "privilege" }, {
            "proceedures", "procedures" }, {
            "pronensiation", "pronunciation" }, {
            "pronisation", "pronunciation" }, {
            "pronounciation", "pronunciation" }, {
            "properally", "properly" }, {
            "proplematic", "problematic" }, {
            "protray", "portray" }, {
            "pscolgst", "psychologist" }, {
            "psicolagest", "psychologist" }, {
            "psycolagest", "psychologist" }, {
            "quoz", "quiz" }, {
            "radious", "radius" }, {
            "ramplily", "rampantly" }, {
            "reccomend", "recommend" }, {
            "reccona", "raccoon" }, {
            "recieve", "receive" }, {
            "reconise", "recognize" }, {
            "rectangeles", "rectangle" }, {
            "redign", "redesign" }, {
            "reoccurring", "recurring" }, {
            "repitition", "repetition" }, {
            "replasments", "replacement" }, {
            "reposable", "responsible" }, {
            "reseblence", "resemblance" }, {
            "respct", "respect" }, {
            "respecally", "respectfully" }, {
            "roon", "room" }, {
            "rought", "roughly" }, {
            "rsx", "RSX" }, {
            "rudemtry", "rudimentary" }, {
            "runnung", "running" }, {
            "sacreligious", "sacrilegious" }, {
            "saftly", "safely" }, {
            "salut", "salute" }, {
            "satifly", "satisfy" }, {
            "scrabdle", "scrabble" }, {
            "searcheable", "searchable" }, {
            "secion", "section" }, {
            "seferal", "several" }, {
            "segements", "segments" }, {
            "sence", "sense" }, {
            "seperate", "separate" }, {
            "sherbert", "sherbet" }, {
            "sicolagest", "psychologist" }, {
            "sieze", "seize" }, {
            "simpfilty", "simplicity" }, {
            "simplye", "simply" }, {
            "singal", "signal" }, {
            "sitte", "site" }, {
            "situration", "situation" }, {
            "slyph", "sylph" }, {
            "smil", "smile" }, {
            "snuck", "sneaked" }, {
            "sometmes", "sometimes" }, {
            "soonec", "sonic" }, {
            "specificialy", "specifically" }, {
            "spel", "spell" }, {
            "spoak", "spoke" }, {
            "sponsered", "sponsored" }, {
            "stering", "steering" }, {
            "straightjacket", "straitjacket" }, {
            "stumach", "stomach" }, {
            "stutent", "student" }, {
            "styleguide", "style guide" }, {
            "subisitions", "substitutions" }, {
            "subjecribed", "subscribed" }, {
            "subpena", "subpoena" }, {
            "substations", "substitutions" }, {
            "suger", "sugar" }, {
            "supercede", "supersede" }, {
            "superfulous", "superfluous" }, {
            "susan", "Susan" }, {
            "swimwear", "swim wear" }, {
            "syncorization", "synchronization" }, {
            "taff", "tough" }, {
            "taht", "that" }, {
            "tattos", "tattoos" }, {
            "techniquely", "technically" }, {
            "teh", "the" }, {
            "tem", "team" }, {
            "teo", "two" }, {
            "teridical", "theoretical" }, {
            "tesst", "test" }, {
            "tets", "tests" }, {
            "thanot", "than or" }, {
            "theirselves", "themselves" }, {
            "theridically", "theoretical" }, {
            "thredically", "theoretically" }, {
            "thruout", "throughout" }, {
            "ths", "this" }, {
            "titalate", "titillate" }, {
            "tobagan", "tobaggon" }, {
            "tommorrow", "tomorrow" }, {
            "tomorow", "tomorrow" }, {
            "tradegy", "tragedy" }, {
            "trubbel", "trouble" }, {
            "ttest", "test" }, {
            "tunnellike", "tunnel like" }, {
            "tured", "turned" }, {
            "tyrrany", "tyranny" }, {
            "unatourral", "unnatural" }, {
            "unaturral", "unnatural" }, {
            "unconisitional", "unconstitutional" }, {
            "unconscience", "unconscious" }, {
            "underladder", "under ladder" }, {
            "unentelegible", "unintelligible" }, {
            "unfortunently", "unfortunately" }, {
            "unnaturral", "unnatural" }, {
            "upcast", "up cast" }, {
            "upmost", "utmost" }, {
            "uranisium", "uranium" }, {
            "verison", "version" }, {
            "vinagarette", "vinaigrette" }, {
            "volumptuous", "voluptuous" }, {
            "volunteerism", "voluntarism" }, {
            "volye", "volley" }, {
            "wadting", "wasting" }, {
            "waite", "wait" }, {
            "wan't", "won't" }, {
            "warloord", "warlord" }, {
            "whaaat", "what" }, {
            "whard", "ward" }, {
            "whimp", "wimp" }, {
            "wicken", "weaken" }, {
            "wierd", "weird" }, {
            "wrank", "rank" }, {
            "writeen", "righten" }, {
            "writting", "writing" }, {
            "wundeews", "windows" }, {
            "yeild", "yield" }, {
            "youe", "your" }
    };

    /**
     * A subset of FIXTURE generated by this test.
     */
    private static final String[][] MATCHES = { { "Accosinly", "Occasionally" }, {
            "Maddness", "Madness" }, {
            "Occusionaly", "Occasionally" }, {
            "Steffen", "Stephen" }, {
            "Thw", "The" }, {
            "Unformanlly", "Unfortunately" }, {
            "Unfortally", "Unfortunately" }, {
            "abilitey", "ability" }, {
            "absorbtion", "absorption" }, {
            "accidently", "accidentally" }, {
            "accomodate", "accommodate" }, {
            "acommadate", "accommodate" }, {
            "acord", "accord" }, {
            "adultry", "adultery" }, {
            "aggresive", "aggressive" }, {
            "alchohol", "alcohol" }, {
            "alchoholic", "alcoholic" }, {
            "allieve", "alive" }, {
            "alot", "a lot" }, {
            "alright", "all right" }, {
            "amature", "amateur" }, {
            "ambivilant", "ambivalent" }, {
            "amourfous", "amorphous" }, {
            "annoint", "anoint" }, {
            "annonsment", "announcement" }, {
            "annoyting", "anting" }, {
            "annuncio", "announce" }, {
            "anotomy", "anatomy" }, {
            "antidesestablishmentarianism", "antidisestablishmentarianism" }, {
            "antidisestablishmentarism", "antidisestablishmentarianism" }, {
            "anynomous", "anonymous" }, {
            "appelet", "applet" }, {
            "appreceiated", "appreciated" }, {
            "appresteate", "appreciate" }, {
            "aquantance", "acquaintance" }, {
            "aricticure", "architecture" }, {
            "asterick", "asterisk" }, {
            "asymetric", "asymmetric" }, {
            "atentively", "attentively" }, {
            "bankrot", "bankrupt" }, {
            "basicly", "basically" }, {
            "batallion", "battalion" }, {
            "bbrose", "browse" }, {
            "beauro", "bureau" }, {
            "beaurocracy", "bureaucracy" }, {
            "beggining", "beginning" }, {
            "behaviour", "behavior" }, {
            "beleive", "believe" }, {
            "belive", "believe" }, {
            "blait", "bleat" }, {
            "bouyant", "buoyant" }, {
            "boygot", "boycott" }, {
            "brocolli", "broccoli" }, {
            "buder", "butter" }, {
            "budr", "butter" }, {
            "budter", "butter" }, {
            "buracracy", "bureaucracy" }, {
            "burracracy", "bureaucracy" }, {
            "buton", "button" }, {
            "byby", "by by" }, {
            "cauler", "caller" }, {
            "ceasar", "caesar" }, {
            "cemetary", "cemetery" }, {
            "changeing", "changing" }, {
            "cheet", "cheat" }, {
            "cimplicity", "simplicity" }, {
            "circumstaces", "circumstances" }, {
            "clob", "club" }, {
            "coaln", "colon" }, {
            "colleaque", "colleague" }, {
            "colloquilism", "colloquialism" }, {
            "columne", "column" }, {
            "comitmment", "commitment" }, {
            "comitte", "committee" }, {
            "comittmen", "commitment" }, {
            "comittmend", "commitment" }, {
            "commerciasl", "commercials" }, {
            "commited", "committed" }, {
            "commitee", "committee" }, {
            "companys", "companies" }, {
            "comupter", "computer" }, {
            "concensus", "consensus" }, {
            "confusionism", "confucianism" }, {
            "congradulations", "congratulations" }, {
            "contunie", "continue" }, {
            "cooly", "coolly" }, {
            "copping", "coping" }, {
            "cosmoplyton", "cosmopolitan" }, {
            "crasy", "crazy" }, {
            "croke", "croak" }, {
            "crucifiction", "crucifixion" }, {
            "crusifed", "crucified" }, {
            "cumba", "combo" }, {
            "custamisation", "customization" }, {
            "dag", "dog" }, {
            "daly", "daily" }, {
            "defence", "defense" }, {
            "definate", "definite" }, {
            "definately", "definitely" }, {
            "dependeble", "dependable" }, {
            "descrption", "description" }, {
            "descrptn", "description" }, {
            "desparate", "desperate" }, {
            "dessicate", "desiccate" }, {
            "destint", "distant" }, {
            "develepment", "developments" }, {
            "developement", "development" }, {
            "develpond", "development" }, {
            "devulge", "divulge" }, {
            "dieties", "deities" }, {
            "dinasaur", "dinosaur" }, {
            "dinasour", "dinosaur" }, {
            "discuess", "discuss" }, {
            "disect", "dissect" }, {
            "disippate", "dissipate" }, {
            "disition", "decision" }, {
            "dispair", "despair" }, {
            "distarct", "distract" }, {
            "distart", "distort" }, {
            "distroy", "destroy" }, {
            "doenload", "download" }, {
            "dongle", "dangle" }, {
            "doog", "dog" }, {
            "dramaticly", "dramatically" }, {
            "drunkeness", "drunkenness" }, {
            "ductioneery", "dictionary" }, {
            "ecstacy", "ecstasy" }, {
            "egsistence", "existence" }, {
            "eitiology", "etiology" }, {
            "elagent", "elegant" }, {
            "embarass", "embarrass" }, {
            "embarassment", "embarrassment" }, {
            "embaress", "embarrass" }, {
            "encapsualtion", "encapsulation" }, {
            "encyclapidia", "encyclopedia" }, {
            "encyclopia", "encyclopedia" }, {
            "engins", "engine" }, {
            "enhence", "enhance" }, {
            "ennuui", "ennui" }, {
            "enventions", "inventions" }, {
            "envireminakl", "environmental" }, {
            "enviroment", "environment" }, {
            "epitomy", "epitome" }, {
            "equire", "acquire" }, {
            "errara", "error" }, {
            "evaualtion", "evaluation" }, {
            "excede", "exceed" }, {
            "excercise", "exercise" }, {
            "excpt", "except" }, {
            "exhileration", "exhilaration" }, {
            "existance", "existence" }, {
            "expleyly", "explicitly" }, {
            "explity", "explicitly" }, {
            "failer", "failure" }, {
            "faver", "favor" }, {
            "faxe", "fax" }, {
            "firey", "fiery" }, {
            "fistival", "festival" }, {
            "flatterring", "flattering" }, {
            "flukse", "flux" }, {
            "fone", "phone" }, {
            "forsee", "foresee" }, {
            "frustartaion", "frustrating" }, {
            "funetik", "phonetic" }, {
            "gaurd", "guard" }, {
            "generly", "generally" }, {
            "ghandi", "gandhi" }, {
            "gotton", "gotten" }, {
            "gracefull", "graceful" }, {
            "gradualy", "gradually" }, {
            "grammer", "grammar" }, {
            "hallo", "hello" }, {
            "hapily", "happily" }, {
            "harrass", "harass" }, {
            "heellp", "help" }, {
            "heighth", "height" }, {
            "hellp", "help" }, {
            "helo", "hello" }, {
            "hifin", "hyphen" }, {
            "hifine", "hyphen" }, {
            "hiphine", "hyphen" }, {
            "hippie", "hippy" }, {
            "hippopotamous", "hippopotamus" }, {
            "hourse", "horse" }, {
            "houssing", "housing" }, {
            "howaver", "however" }, {
            "howver", "however" }, {
            "humaniti", "humanity" }, {
            "hyfin", "hyphen" }, {
            "hystrical", "hysterical" }, {
            "illegitament", "illegitimate" }, {
            "imbed", "embed" }, {
            "imediaetly", "immediately" }, {
            "immenant", "immanent" }, {
            "implemtes", "implements" }, {
            "inadvertant", "inadvertent" }, {
            "incase", "in case" }, {
            "incedious", "insidious" }, {
            "incompleet", "incomplete" }, {
            "incomplot", "incomplete" }, {
            "inconvenant", "inconvenient" }, {
            "inconvience", "inconvenience" }, {
            "independant", "independent" }, {
            "independenent", "independent" }, {
            "indepnends", "independent" }, {
            "indepth", "in depth" }, {
            "indispensible", "indispensable" }, {
            "inefficite", "inefficient" }, {
            "infact", "in fact" }, {
            "influencial", "influential" }, {
            "innoculate", "inoculate" }, {
            "insistant", "insistent" }, {
            "insistenet", "insistent" }, {
            "instulation", "installation" }, {
            "intealignt", "intelligent" }, {
            "intelegent", "intelligent" }, {
            "intelegnent", "intelligent" }, {
            "intelejent", "intelligent" }, {
            "inteligent", "intelligent" }, {
            "intelignt", "intelligent" }, {
            "intellagant", "intelligent" }, {
            "intellegent", "intelligent" }, {
            "intellegint", "intelligent" }, {
            "intellgnt", "intelligent" }, {
            "intensionality", "intensionally" }, {
            "internation", "international" }, {
            "interpretate", "interpret" }, {
            "interpretter", "interpreter" }, {
            "intertes", "interested" }, {
            "intertesd", "interested" }, {
            "invermeantial", "environmental" }, {
            "irresistable", "irresistible" }, {
            "irritible", "irritable" }, {
            "isreal", "israel" }, {
            "johhn", "john" }, {
            "kippur", "kipper" }, {
            "knawing", "knowing" }, {
            "lesure", "leisure" }, {
            "liasion", "lesion" }, {
            "liason", "liaison" }, {
            "likly", "likely" }, {
            "liquify", "liquefy" }, {
            "lloyer", "layer" }, {
            "lossing", "losing" }, {
            "luser", "laser" }, {
            "maintanence", "maintenance" }, {
            "mandelbrot", "Mandelbrot" }, {
            "marshall", "marshal" }, {
            "maxium", "maximum" }, {
            "mic", "mike" }, {
            "midia", "media" }, {
            "millenium", "millennium" }, {
            "miniscule", "minuscule" }, {
            "minkay", "monkey" }, {
            "mischievious", "mischievous" }, {
            "momento", "memento" }, {
            "monkay", "monkey" }, {
            "mosaik", "mosaic" }, {
            "mostlikely", "most likely" }, {
            "mousr", "mouser" }, {
            "mroe", "more" }, {
            "necesary", "necessary" }, {
            "necesser", "necessary" }, {
            "neice", "niece" }, {
            "neighbour", "neighbor" }, {
            "nemonic", "pneumonic" }, {
            "nevade", "Nevada" }, {
            "nickleodeon", "nickelodeon" }, {
            "nieve", "naive" }, {
            "noone", "no one" }, {
            "notin", "not in" }, {
            "nozled", "nuzzled" }, {
            "objectsion", "objects" }, {
            "ocassion", "occasion" }, {
            "occuppied", "occupied" }, {
            "occurence", "occurrence" }, {
            "octagenarian", "octogenarian" }, {
            "opposim", "opossum" }, {
            "organise", "organize" }, {
            "organiz", "organize" }, {
            "orientate", "orient" }, {
            "oscilascope", "oscilloscope" }, {
            "parametic", "parameter" }, {
            "permissable", "permissible" }, {
            "permmasivie", "permissive" }, {
            "persue", "pursue" }, {
            "phantasia", "fantasia" }, {
            "phenominal", "phenomenal" }, {
            "playwrite", "playwright" }, {
            "poeses", "poesies" }, {
            "poligamy", "polygamy" }, {
            "politict", "politic" }, {
            "pollice", "police" }, {
            "polypropalene", "polypropylene" }, {
            "possable", "possible" }, {
            "practicle", "practical" }, {
            "pragmaticism", "pragmatism" }, {
            "preceeding", "preceding" }, {
            "precios", "precision" }, {
            "preemptory", "peremptory" }, {
            "prefixt", "prefixed" }, {
            "presbyterian", "Presbyterian" }, {
            "presue", "pursue" }, {
            "presued", "pursued" }, {
            "privielage", "privilege" }, {
            "priviledge", "privilege" }, {
            "proceedures", "procedures" }, {
            "pronensiation", "pronunciation" }, {
            "pronounciation", "pronunciation" }, {
            "properally", "properly" }, {
            "proplematic", "problematic" }, {
            "protray", "portray" }, {
            "pscolgst", "psychologist" }, {
            "psicolagest", "psychologist" }, {
            "psycolagest", "psychologist" }, {
            "quoz", "quiz" }, {
            "radious", "radius" }, {
            "reccomend", "recommend" }, {
            "reccona", "raccoon" }, {
            "recieve", "receive" }, {
            "reconise", "recognize" }, {
            "rectangeles", "rectangle" }, {
            "reoccurring", "recurring" }, {
            "repitition", "repetition" }, {
            "replasments", "replacement" }, {
            "respct", "respect" }, {
            "respecally", "respectfully" }, {
            "rsx", "RSX" }, {
            "runnung", "running" }, {
            "sacreligious", "sacrilegious" }, {
            "salut", "salute" }, {
            "searcheable", "searchable" }, {
            "seferal", "several" }, {
            "segements", "segments" }, {
            "sence", "sense" }, {
            "seperate", "separate" }, {
            "sicolagest", "psychologist" }, {
            "sieze", "seize" }, {
            "simplye", "simply" }, {
            "sitte", "site" }, {
            "slyph", "sylph" }, {
            "smil", "smile" }, {
            "sometmes", "sometimes" }, {
            "soonec", "sonic" }, {
            "specificialy", "specifically" }, {
            "spel", "spell" }, {
            "spoak", "spoke" }, {
            "sponsered", "sponsored" }, {
            "stering", "steering" }, {
            "straightjacket", "straitjacket" }, {
            "stumach", "stomach" }, {
            "stutent", "student" }, {
            "styleguide", "style guide" }, {
            "subpena", "subpoena" }, {
            "substations", "substitutions" }, {
            "supercede", "supersede" }, {
            "superfulous", "superfluous" }, {
            "susan", "Susan" }, {
            "swimwear", "swim wear" }, {
            "syncorization", "synchronization" }, {
            "taff", "tough" }, {
            "taht", "that" }, {
            "tattos", "tattoos" }, {
            "techniquely", "technically" }, {
            "teh", "the" }, {
            "tem", "team" }, {
            "teo", "two" }, {
            "teridical", "theoretical" }, {
            "tesst", "test" }, {
            "theridically", "theoretical" }, {
            "thredically", "theoretically" }, {
            "thruout", "throughout" }, {
            "ths", "this" }, {
            "titalate", "titillate" }, {
            "tobagan", "tobaggon" }, {
            "tommorrow", "tomorrow" }, {
            "tomorow", "tomorrow" }, {
            "trubbel", "trouble" }, {
            "ttest", "test" }, {
            "tyrrany", "tyranny" }, {
            "unatourral", "unnatural" }, {
            "unaturral", "unnatural" }, {
            "unconisitional", "unconstitutional" }, {
            "unconscience", "unconscious" }, {
            "underladder", "under ladder" }, {
            "unentelegible", "unintelligible" }, {
            "unfortunently", "unfortunately" }, {
            "unnaturral", "unnatural" }, {
            "upcast", "up cast" }, {
            "verison", "version" }, {
            "vinagarette", "vinaigrette" }, {
            "volunteerism", "voluntarism" }, {
            "volye", "volley" }, {
            "waite", "wait" }, {
            "wan't", "won't" }, {
            "warloord", "warlord" }, {
            "whaaat", "what" }, {
            "whard", "ward" }, {
            "whimp", "wimp" }, {
            "wicken", "weaken" }, {
            "wierd", "weird" }, {
            "wrank", "rank" }, {
            "writeen", "righten" }, {
            "writting", "writing" }, {
            "wundeews", "windows" }, {
            "yeild", "yield" }, };

    public static Test suite() {
        return new TestSuite(DoubleMetaphoneTest.class);
    }

    private DoubleMetaphone doubleMetaphone = null;

    public DoubleMetaphoneTest(String name) {
        super(name);
    }

    /**
     * Tests encoding APIs in one place.
     */
    private void assertDoubleMetaphone(String expected, String source) {
        assertEquals(expected, this.getDoubleMetaphone().encode(source));
        try {
            assertEquals(expected, this.getDoubleMetaphone().encode((Object) source));
        } catch (EncoderException e) {
            fail("Unexpected expection: " + e);
        }
        assertEquals(expected, this.getDoubleMetaphone().doubleMetaphone(source));
        assertEquals(expected, this.getDoubleMetaphone().doubleMetaphone(source, false));
    }

    /**
     * Tests encoding APIs in one place.
     */
    public void assertDoubleMetaphoneAlt(String expected, String source) {
        assertEquals(expected, this.getDoubleMetaphone().doubleMetaphone(source, true));
    }

    public void doubleMetaphoneEqualTest(String[][] pairs, boolean useAlternate) {
        this.validateFixture(pairs);
        for (int i = 0; i < pairs.length; i++) {
            String name0 = pairs[i][0];
            String name1 = pairs[i][1];
            String failMsg = "Expected match between " + name0 + " and " + name1 + " (use alternate: " + useAlternate + ")";
            assertTrue(failMsg, this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1, useAlternate));
            assertTrue(failMsg, this.getDoubleMetaphone().isDoubleMetaphoneEqual(name1, name0, useAlternate));
            if (!useAlternate) {
                assertTrue(failMsg, this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1));
                assertTrue(failMsg, this.getDoubleMetaphone().isDoubleMetaphoneEqual(name1, name0));
            }
        }
    }

    public void doubleMetaphoneNotEqualTest(boolean alternate) {
        assertFalse(this.getDoubleMetaphone().isDoubleMetaphoneEqual("Brain", "Band", alternate));
        assertFalse(this.getDoubleMetaphone().isDoubleMetaphoneEqual("Band", "Brain", alternate));

        if (!alternate) {
            assertFalse(this.getDoubleMetaphone().isDoubleMetaphoneEqual("Brain", "Band"));
            assertFalse(this.getDoubleMetaphone().isDoubleMetaphoneEqual("Band", "Brain"));
        }
    }

    private DoubleMetaphone getDoubleMetaphone() {
        return this.doubleMetaphone;
    }

    protected StringEncoder makeEncoder() {
        return new Metaphone();
    }

    private void setDoubleMetaphone(DoubleMetaphone doubleMetaphone) {
        this.doubleMetaphone = doubleMetaphone;
    }

    public void setUp() throws Exception {
        super.setUp();
        this.setDoubleMetaphone(new DoubleMetaphone());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        this.setDoubleMetaphone(null);
    }

    public void testDoubleMetaphone() {
        assertDoubleMetaphone("TSTN", "testing");
        assertDoubleMetaphone("0", "The");
        assertDoubleMetaphone("KK", "quick");
        assertDoubleMetaphone("PRN", "brown");
        assertDoubleMetaphone("FKS", "fox");
        assertDoubleMetaphone("JMPT", "jumped");
        assertDoubleMetaphone("AFR", "over");
        assertDoubleMetaphone("0", "the");
        assertDoubleMetaphone("LS", "lazy");
        assertDoubleMetaphone("TKS", "dogs");
        assertDoubleMetaphone("MKFR", "MacCafferey");
        assertDoubleMetaphone("STFN", "Stephan");
        assertDoubleMetaphone("KSSK", "Kuczewski");
        assertDoubleMetaphone("MKLL", "McClelland");
        assertDoubleMetaphone("SNHS", "san jose");
        assertDoubleMetaphone("SNFP", "xenophobia");

        assertDoubleMetaphoneAlt("TSTN", "testing");
        assertDoubleMetaphoneAlt("T", "The");
        assertDoubleMetaphoneAlt("KK", "quick");
        assertDoubleMetaphoneAlt("PRN", "brown");
        assertDoubleMetaphoneAlt("FKS", "fox");
        assertDoubleMetaphoneAlt("AMPT", "jumped");
        assertDoubleMetaphoneAlt("AFR", "over");
        assertDoubleMetaphoneAlt("T", "the");
        assertDoubleMetaphoneAlt("LS", "lazy");
        assertDoubleMetaphoneAlt("TKS", "dogs");
        assertDoubleMetaphoneAlt("MKFR", "MacCafferey");
        assertDoubleMetaphoneAlt("STFN", "Stephan");
        assertDoubleMetaphoneAlt("KXFS", "Kutchefski");
        assertDoubleMetaphoneAlt("MKLL", "McClelland");
        assertDoubleMetaphoneAlt("SNHS", "san jose");
        assertDoubleMetaphoneAlt("SNFP", "xenophobia");
    }

    public void testEmpty() {
        assertEquals(null, this.getDoubleMetaphone().doubleMetaphone(null));
        assertEquals(null, this.getDoubleMetaphone().doubleMetaphone(""));
        assertEquals(null, this.getDoubleMetaphone().doubleMetaphone(" "));
        assertEquals(null, this.getDoubleMetaphone().doubleMetaphone("\t\n\r "));
    }

    /**
     * Test setting maximum length
     */
    public void testSetMaxCodeLength() {
        String value = "jumped";
        
        DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

        // Sanity check of default settings
        assertEquals("Default Max Code Length", 4, doubleMetaphone.getMaxCodeLen());
        assertEquals("Default Primary",   "JMPT", doubleMetaphone.doubleMetaphone(value, false));
        assertEquals("Default Alternate", "AMPT", doubleMetaphone.doubleMetaphone(value, true));

        // Check setting Max Code Length
        doubleMetaphone.setMaxCodeLen(3);
        assertEquals("Set Max Code Length", 3, doubleMetaphone.getMaxCodeLen());
        assertEquals("Max=3 Primary",   "JMP", doubleMetaphone.doubleMetaphone(value, false));
        assertEquals("Max=3 Alternate", "AMP", doubleMetaphone.doubleMetaphone(value, true));
    }

    public void testIsDoubleMetaphoneEqualBasic() {
        String[][] testFixture = new String[][] { { "Case", "case" }, {
                "CASE", "Case" }, {
                "caSe", "cAsE" }, {
                "cookie", "quick" }, {
                "quick", "cookie" }, {
                "Brian", "Bryan" }, {
                "Auto", "Otto" }, {
                "Steven", "Stefan" }, {
                "Philipowitz", "Filipowicz" }
        };
        doubleMetaphoneEqualTest(testFixture, false);
        doubleMetaphoneEqualTest(testFixture, true);
    }

    /**
     * Example in the original article but failures in this Java impl:
     */
    public void testIsDoubleMetaphoneEqualExtended1() {
        //        String[][] testFixture = new String[][] { { "Smith", "Schmidt" }
        //        };
        //        doubleMetaphoneEqualTest(testFixture, false);
        //        doubleMetaphoneEqualTest(testFixture, true);
    }

    public void testIsDoubleMetaphoneEqualExtended2() {
        String[][] testFixture = new String[][] { { "Jablonski", "Yablonsky" }
        };
        //doubleMetaphoneEqualTest(testFixture, false);
        doubleMetaphoneEqualTest(testFixture, true);
    }

    /**
     * Used to generate the MATCHES array and test possible matches from the
     * FIXTURE arrary.
     */
    public void testIsDoubleMetaphoneEqualExtended3() {
        this.validateFixture(FIXTURE);
        StringBuffer failures = new StringBuffer();
        StringBuffer matches = new StringBuffer();
        String cr = System.getProperty("line.separator");
        matches.append("private static final String[][] MATCHES = {" + cr);
        int failCount = 0;
        for (int i = 0; i < FIXTURE.length; i++) {
            String name0 = FIXTURE[i][0];
            String name1 = FIXTURE[i][1];
            boolean match1 = this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1, false);
            boolean match2 = this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1, true);
            if (match1 == false && match2 == false) {
                String failMsg = "[" + i + "] " + name0 + " and " + name1 + cr;
                failures.append(failMsg);
                failCount++;
            } else {
                matches.append("{\"" + name0 + "\", \"" + name1 + "\"}," + cr);
            }
        }
        String msg = failures.toString();
        matches.append("};");
        // Turn on to print a new MATCH array
        //System.out.println(matches.toString());
        if (msg.length() > 0) {
            // Turn on to see which pairs do NOT match.
            //fail(failCount + " failures out of " + FIXTURE.length + ". The
            // following could be made to match: " + cr + msg);
        }
    }

    public void testIsDoubleMetaphoneEqualWithMATCHES() {
        this.validateFixture(MATCHES);
        for (int i = 0; i < MATCHES.length; i++) {
            String name0 = MATCHES[i][0];
            String name1 = MATCHES[i][1];
            boolean match1 = this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1, false);
            boolean match2 = this.getDoubleMetaphone().isDoubleMetaphoneEqual(name0, name1, true);
            if (match1 == false && match2 == false) {
                fail("Expected match [" + i + "] " + name0 + " and " + name1);
            }
        }
    }

    public void testIsDoubleMetaphoneNotEqual() {
        doubleMetaphoneNotEqualTest(false);
        doubleMetaphoneNotEqualTest(true);
    }

    public void testCCedilla() {
        this.getDoubleMetaphone().isDoubleMetaphoneEqual("ç", "S");
    }
    
    public void testNTilde() {
        this.getDoubleMetaphone().isDoubleMetaphoneEqual("ñ", "N");
    }
    
    public void validateFixture(String[][] pairs) {
        if (pairs.length == 0) {
            fail("Test fixture is empty");
        }
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i].length != 2) {
                fail("Error in test fixture in the data array at index " + i);
            }
        }
    }
}
