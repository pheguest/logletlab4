<html>
<head>
<style>
p.normal {
    font-style: normal;
}

p.italic {
    font-style: italic;
}

p.oblique {
    font-style: oblique;
}

p.largeTitle{
    font-style: normal;
    font-size: 2.5em;
    font-weight: bold;
}
p.smallTitle{
    font-style: oblique;
    font-size: 1.0em;
    font-weight: bold;
}


</style>
</head>
  <body>


<form action="loglet.php" method="POST" enctype="multipart/form-data">
<p class="largeTitle">Loglet Lab - Version 4.1 (Alpha)</p>
		<img width="300" src="images/RU_logo_tagline.jpg"> 
<p class="smallTitle">Single logistic regression</p>
<input type="file" name="csvUpload" id="fileUp" />
<br><br>

Line color : <input type="radio" name="group1" value="red">Red &nbsp; <input type="radio" name="group1" value="orange">Orange &nbsp; <input type="radio" name="group1" value="yellow">Yellow &nbsp; <input type="radio" name="group1" value="green">Green &nbsp; <input type="radio" name="group1" value="blue">Blue &nbsp; <input type="radio" name="group1" value="aquamarine">Aqua &nbsp; <input type="radio" name="group1" value="magenta">Magenta &nbsp; <input type="radio" name="group1" value="antiquewhite">Beige &nbsp; <input type="radio" name="group1" value="darkgray">Gray &nbsp; <input type="radio" name="group1" value="black">Black <br><br>

X-axis font color: <input type="radio" name="group2" value="red">Red &nbsp; <input type="radio" name="group2" value="orange">Orange &nbsp; <input type="radio" name="group2" value="yellow">Yellow &nbsp; <input type="radio" name="group2" value="green">Green &nbsp; <input type="radio" name="group2" value="blue">Blue &nbsp; <input type="radio" name="group2" value="aquamarine">Aqua &nbsp; <input type="radio" name="group2" value="magenta">Magenta &nbsp; <input type="radio" name="group2" value="antiquewhite">Beige &nbsp; <input type="radio" name="group2" value="darkgray">Gray &nbsp; <input type="radio" name="group2" value="black">Black <br><br>

Y-axis font color: <input type="radio" name="group3" value="red">Red &nbsp; <input type="radio" name="group3" value="orange">Orange &nbsp; <input type="radio" name="group3" value="yellow">Yellow &nbsp; <input type="radio" name="group3" value="green">Green &nbsp; <input type="radio" name="group3" value="blue">Blue &nbsp; <input type="radio" name="group3" value="aquamarine">Aqua &nbsp; <input type="radio" name="group3" value="magenta">Magenta &nbsp; <input type="radio" name="group3" value="antiquewhite">Beige &nbsp; <input type="radio" name="group3" value="darkgray">Gray &nbsp; <input type="radio" name="group3" value="black">Black <br><br>

Point color: <input type="radio" name="group4" value="red">Red &nbsp; <input type="radio" name="group4" value="orange">Orange &nbsp; <input type="radio" name="group4" value="yellow">Yellow &nbsp; <input type="radio" name="group4" value="green">Green &nbsp; <input type="radio" name="group4" value="blue">Blue &nbsp; <input type="radio" name="group4" value="aquamarine">Aqua &nbsp; <input type="radio" name="group4" value="magenta">Magenta &nbsp; <input type="radio" name="group4" value="antiquewhite">Beige &nbsp; <input type="radio" name="group4" value="darkgray">Gray &nbsp; <input type="radio" name="group4" value="black">Black <br><br>

Title color: <input type="radio" name="group5" value="red">Red &nbsp; <input type="radio" name="group5" value="orange">Orange &nbsp; <input type="radio" name="group5" value="yellow">Yellow &nbsp; <input type="radio" name="group5" value="green">Green &nbsp; <input type="radio" name="group5" value="blue">Blue &nbsp; <input type="radio" name="group5" value="aquamarine">Aqua &nbsp; <input type="radio" name="group5" value="magenta">Magenta &nbsp; <input type="radio" name="group5" value="antiquewhite">Beige &nbsp; <input type="radio" name="group5" value="darkgray">Gray &nbsp; <input type="radio" name="group5" value="black">Black <br><br>

Title text: <input type="text" name="plotTitle"><br>

		<br><input type="submit" name="submit" value="Submit"><br>
</form>

  </body>

</html>

<?php

//If on submit of form, grab the input parameters of the graph (color, font, file, etc...)
if(isset($_POST['submit'])){

//Assign the input parameters to variables 
$color1 = $_REQUEST[group1];
$color2 = $_REQUEST[group2];
$color3 = $_REQUEST[group3];
$color4 = $_REQUEST[group4];
$color5 = $_REQUEST[group5];
$plotTitle = $_REQUEST[plotTitle];

//Create a unique subdirectory (based on timestamp) for the run.
  $myTimestamp = time();
  exec("mkdir ./$myTimestamp");

//Move the uploaded file to the subdirectory.
  $thefile = $_FILES["csvUpload"]["name"];
  
  move_uploaded_file($_FILES["csvUpload"]["tmp_name"],
      "$myTimestamp/" . $myTimestamp . _ . $_FILES["csvUpload"]["name"]);

/Give the file a unique filename.
  $csvFile = $myTimestamp . _ .$_FILES["csvUpload"]["name"];

//Copy the master R script to the subdirectory.  This one will be the one that is run.  It will be unqiue to the job, so it can be debugged efficiently if necessary. 
  exec("cp /home/www/html/biostat/newPlot.R ./$myTimestamp/$myTimestamp.R");

//Use sed to copy all the input variables to the unique script.
  exec("sed -i '1i plotTitle <- \"$plotTitle\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color5 <- \"$color5\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color4 <- \"$color4\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color3 <- \"$color3\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color2 <- \"$color2\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color1 <- \"$color1\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i fileUpload <- \"/home/www/html/biostat/$myTimestamp/$csvFile\"' ./$myTimestamp/$myTimestamp.R");
 
//Execute the unique R script.  Send the output to a log file, out.txt, so that it isn't lost.  Good for debugging. 
  $outputShell = exec("/usr/local/bin/R-3.1.1/bin/R --no-save < /home/www/html/biostat/$myTimestamp/$myTimestamp.R > /home/www/html/biostat/$myTimestamp/out.txt");

//Sleep for 5 seconds.  This is a safety precaution just in case the R script isn't done running.  This might be able to be omitted. 
  sleep(5);
 
//Move any plots from the run into the unique job directory. 
  exec("mv Rplots.pdf ./$myTimestamp/");
  exec("mv rplotsunflower.pdf ./$myTimestamp/");
  exec("mv rplotsunflower.jpg ./$myTimestamp/");

//Zip up the result file.
  $myZipFile = "$myTimestamp.zip";
  exec("zip -r /home/www/html/biostat/$myTimestamp/$myZipFile ./$myTimestamp/rplotsunflower.pdf");

//Display a link to the result zip file so that user can download from the webpage.  
  echo "<br><a href=\"$myTimestamp/$myZipFile\">DOWNLOAD RESULTS</a><br>";

}
else{
  //Else submit was not clicked and nothing was processed on the page.
  //echo "<br>nothing<br>";
}

?>
