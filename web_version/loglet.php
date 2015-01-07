<html>
<head>

<!-- INCLUDE THE JAVASCRIPT FILES FOR COLOR PICKER AND CUSTOMIZED SHOW/HIDE ELEMENTS -->
<script type="text/javascript" src="jscolor/jscolor.js"></script>
<link rel="stylesheet" type="text/css" href="ShowHide.css" />

<!-- CHECK IF THIS FORM HAS BEEN SUBMITTED -->
<?php
if(isset($_POST['submit'])){
  $submitted = "1";
  if($_REQUEST['group6'] == 'rdoSL')
  {
    $analysisCheck="SL";
  }
  elseif($_REQUEST['group6'] == 'rdoBL')
  {
    $analysisCheck="BL";
  }
  elseif($_REQUEST['group6'] == 'rdoTL')
  {
    $analysisCheck="TL";
  }
}
?>

<script type="text/javascript" src="ShowHide.js"></script>

</script>

<!-- SOME LOCAL STYLES.  COULD BE MOVED INTO A SEPRATE FILE. -->
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

<!-- THE FORM AS SEEN ON THE PAGE -->

<form action="loglet.php" method="POST" enctype="multipart/form-data">
<p class="largeTitle">Loglet Lab - Version 4.0 (alpha)</p>
		<img width="300" src="images/RU_logo_tagline.jpg"> 
<p class="smallTitle">Plotting logistics</p><br>
Data file upload (.csv, .xls accepted): <input type="file" name="csvUpload" id="fileUp" />
<br><br>

<br>

        <label>Analysis type:</label>
        <input type="radio" name="group6" value="rdoSL" id="rdoSL" onclick="showHide(this)" <?php echo ($_REQUEST['group6'] == 'rdoSL') ? 'checked="checked"' : ''; ?> />Single Logistic &nbsp;
        <input type="radio" name="group6" value="rdoBL" id="rdoBL" onclick="showHide(this)" <?php echo ($_REQUEST['group6'] == 'rdoBL') ? 'checked="checked"' : ''; ?> />Bi Logistic &nbsp;
        <input type="radio" name="group6" value="rdoTL" id="rdoTL" onclick="showHide(this)" <?php echo ($_REQUEST['group6'] == 'rdoTL') ? 'checked="checked"' : ''; ?> />Tri Logistic<br><br>
        <table border='0'>
        <tr>
        <td>
        <label for="gr1" id="gr1label" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Growth Rate 1 (a1):</label>
        <input type="text" name="gr1" id="gr1" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[gr1]" ?>">
	</td>
        <td>
        <label for="cp1" id="cp1label" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Carrying Capacity 1 (k1):</label>
        <input type="text" name="cp1" id="cp1" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[cp1]" ?>">
	</td>
        <td>
        <label for="mp1" id="mp1label" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Midpoint 1 (b1):</label>
        <input type="text" name="mp1" id="mp1" <?php if($analysisCheck =="SL" || $analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[mp1]" ?>">
        </td>
        </tr>
	<tr> 
        <td>
        <label for="gr2" id="gr2label" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Growth Rate 2 (a2):</label>
        <input type="text" name="gr2" id="gr2" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[gr2]" ?>">
	</td>
        <td>
        <label for="cp2" id="cp2label" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Carrying Capacity 2 (k2):</label>
        <input type="text" name="cp2" id="cp2" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[cp2]" ?>">
	</td>
        <td>
        <label for="mp2" id="mp2label" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Midpoint 2 (b2):</label>
        <input type="text" name="mp2" id="mp2" <?php if($analysisCheck =="BL" || $analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[mp2]" ?>">
	</td>  
        </tr>
        <tr>
        <td>
	<label for="gr3" id="gr3label" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Growth Rate 3 (a3):</label>
        <input type="text" name="gr3" id="gr3" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[gr3]" ?>">
	</td>
        <td>
        <label for="cp3" id="cp3label" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Carrying Capacity 3 (k3):</label>
        <input type="text" name="cp3" id="cp3" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[cp3]" ?>">
	</td>
        <td>
        <label for="mp3" id="mp3label" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> >Midpoint 3 (b3):</label>
        <input type="text" name="mp3" id="mp3" <?php if($analysisCheck =="TL"){echo "";}else{echo "class='hide'";} ?> value="<?php echo "$_REQUEST[mp3]" ?>">
        </td>
        </tr>
        </table>

<table border='0'>
<tr>
<td>Line color </td><td><input type="text" name="group1" class="color" value="<?php echo "$_REQUEST[group1]"; ?> "></td>
</tr>
<tr>
<td>X-axis font color </td><td><input type="text" name="group2" class="color" value="<?php echo "$_REQUEST[group2]"; ?> "></td>
</tr>
<tr>
<td>Y-axis font color </td><td><input type="text" name="group3" class="color" value="<?php echo "$_REQUEST[group3]"; ?> "></td>
</tr>
<tr>
<td>Point color </td><td><input type="text" name="group4" class="color" value="<?php echo "$_REQUEST[group4]"; ?> "></td>
</tr>
<tr>
<td>Title color </td><td><input type="text" name="group5" class="color" value="<?php echo "$_REQUEST[group5]"; ?> "></td>
</tr>
</table>
<br>

Title font : <input type="radio" name="group7" value="GillSans">Gill Sans &nbsp; <input type="radio" name="group7" value="serif">Serif &nbsp; <input type="radio" name="group7" value="Helvetica">Helvetica &nbsp; <input type="radio" name="group7" value="Palatino">Palatino &nbsp; <input type="radio" name="group7" value="Times">Times &nbsp; <input type="radio" name="group7" value="Bookman">Bookman &nbsp; <input type="radio" name="group7" value="Courier">Courier &nbsp; <input type="radio" name="group7" value="NimbusSan">Nimbus San<br><br>

Title text : <input type="text" name="plotTitle"><br>

		<br><input type="submit" name="submit" value="Submit"><br>
</form>

  </body>

</html>

<?php

//INCLUDE THE EXCEL READER SO THAT .XLS FILES CAN BE ACCEPTED
include("Excel/reader.php");

//IF THE FORM HAS BEEN SUBMITTED
if(isset($_POST['submit'])){

//DEFINE COLORS, TITLES
$color1 = "#" . $_REQUEST[group1];
$color2 = "#" . $_REQUEST[group2];
$color3 = "#" . $_REQUEST[group3];
$color4 = "#" . $_REQUEST[group4];
$color5 = "#" . $_REQUEST[group5];
$titleFont = $_REQUEST[group7];
$plotTitle = $_REQUEST[plotTitle];

//DEFINE WHAT TYPE OF ANALYSIS THIS IS
if($_REQUEST[group6] == "rdoSL")
{
  $analysisType = "single";
}
elseif($_REQUEST[group6] == "rdoBL")
{
  $analysisType = "bilogistic";
}
elseif($_REQUEST[group6] == "rdoTL")
{
  $analysisType = "trilogistic";
}

//GET THE A,K,B(s) FROM THE FORM
$growthRate1 = $_REQUEST[gr1];
$carryingCap1 = $_REQUEST[cp1];
$midpoint1 = $_REQUEST[mp1];
$growthRate2 = $_REQUEST[gr2];
$carryingCap2 = $_REQUEST[cp2];
$midpoint2 = $_REQUEST[mp2];
$growthRate3 = $_REQUEST[gr3];
$carryingCap3 = $_REQUEST[cp3];
$midpoint3 = $_REQUEST[mp3];


//MAKE A SUBDIRECTORY FOR THIS PARTICULAR JOB
  $myTimestamp = time();
  exec("mkdir ./$myTimestamp");

//MOVE THE INPUT FILE TO THE RIGHT SUBDIRECTORY
$file_parts = pathinfo($thefile);

  $thefile = $_FILES["csvUpload"]["name"];

  $ext = substr(strrchr($thefile, '.'), 1);

  move_uploaded_file($_FILES["csvUpload"]["tmp_name"],
      "$myTimestamp/" . $myTimestamp . _ . $_FILES["csvUpload"]["name"]);

  $csvFile = $myTimestamp . _ .$_FILES["csvUpload"]["name"];

//MAKE A COPY OF THE R SCRIPT AND PASS IT INTO THE SUBDIRECTORY.  THE VALUES WILL THEN BE CODED INTO THIS SCRIPT.
  exec("cp /home/www/html/biostat/newPlot.R ./$myTimestamp/$myTimestamp.R");

//WRITE IN THE USER INPUT VALUES INTO THE WORKING COPY OF THE R SCRIPT. (TITLES, COLORS, ETC...)
  if($ext == "xls")
  {
    exec("sed -i '1i file_ext <- \"xls\"' ./$myTimestamp/$myTimestamp.R");
  }
  else if($ext == "csv")
  {
    exec("sed -i '1i file_ext <- \"csv\"' ./$myTimestamp/$myTimestamp.R");
  }

  exec("sed -i '1i plotTitle <- \"$plotTitle\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color5 <- \"$color5\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color4 <- \"$color4\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color3 <- \"$color3\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color2 <- \"$color2\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i color1 <- \"$color1\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i fileUpload <- \"/home/www/html/biostat/$myTimestamp/$csvFile\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i analysisType <- \"$analysisType\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i growthRate1 <- \"$growthRate1\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i carryingCap1 <- \"$carryingCap1\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i midpoint1 <- \"$midpoint1\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i growthRate2 <- \"$growthRate2\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i carryingCap2 <- \"$carryingCap2\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i midpoint2 <- \"$midpoint2\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i growthRate3 <- \"$growthRate3\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i carryingCap3 <- \"$carryingCap3\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i midpoint3 <- \"$midpoint3\"' ./$myTimestamp/$myTimestamp.R");
  exec("sed -i '1i titleFont <- \"$titleFont\"' ./$myTimestamp/$myTimestamp.R");

//EXECUTE THE WORKING COPY
  $outputShell = exec("R --no-save < /home/www/html/biostat/$myTimestamp/$myTimestamp.R > /home/www/html/biostat/$myTimestamp/out.log");

//SLEEP FOR A FEW SECONDS TO LET THE R SCRIPT FINISH (JUST IN CASE...)
sleep(5);

//MOVE THE RESULTING OUTPUT FILES INTO THE SUBDIRECTORIES
  exec("mv Rplots.pdf ./$myTimestamp/"); //This should not be needed.  If Rplots is created, check the image generation method.
  exec("mv singleRegression.pdf ./$myTimestamp/");
  exec("mv singleRegression.jpg ./$myTimestamp/");
  exec("mv timeSeries.pdf ./$myTimestamp/");
  exec("mv timeSeries.jpg ./$myTimestamp/");
  exec("mv ggplotTimeSeries.jpg ./$myTimestamp/");
  exec("mv ggplotTimeSeries.pdf ./$myTimestamp/");
  exec("mv normalDistCurve.pdf ./$myTimestamp/");
  exec("mv normalDistCurve.jpg ./$myTimestamp/");
  exec("mv ggplotNormDist.pdf ./$myTimestamp/");
  exec("mv ggplotNormDist.jpg ./$myTimestamp/");
  exec("mv BilogisticCurve.pdf ./$myTimestamp/");
  exec("mv BilogisticCurve.jpg ./$myTimestamp/");
  exec("mv TrilogisticCurve.pdf ./$myTimestamp/");
  exec("mv TrilogisticCurve.jpg ./$myTimestamp/");
  exec("mv LogisticRegression.pdf ./$myTimestamp/");
  exec("mv residuals.txt ./$myTimestamp/");
  exec("mv ggplotResiduals.pdf ./$myTimestamp/");
  exec("mv ggplotResiduals.jpg ./$myTimestamp/");
  exec("mv standardizedResiduals.jpg ./$myTimestamp/");
  exec("mv ggplotStandardResiduals.jpg ./$myTimestamp/");
  exec("mv standardizedResiduals.pdf ./$myTimestamp/");
  exec("mv ggplotStandardResiduals.pdf ./$myTimestamp/");
  exec("mv ggplotNLS.jpg ./$myTimestamp/");
  exec("mv ggplotNLS.pdf ./$myTimestamp/");

//ZIP UP ALL THE FILES TO BE RETURNED TO THE USER
  $myZipFile = "$myTimestamp.zip";
  exec("zip -r /home/www/html/biostat/$myTimestamp/$myZipFile ./$myTimestamp/*.pdf ./$myTimestamp/*.jpg ./$myTimestamp/residuals.txt");

//DISPLAY THE INPUT FILE TO THE SCREEN FOR THE USER TO MAKE SURE IT LOADED CORRECTLY. (HANDLED SLIGHTLY DIFFERENTLY IF XLS OR CSV)
  if($ext == "xls")
  {
    $excel = new Spreadsheet_Excel_Reader();
    $excel->read("/home/www/html/biostat/$myTimestamp/$csvFile");
    echo "<table>";
    for ($i = 1; $i <= $excel->sheets[0]['numRows']; $i++) {
	echo "<tr>";
        for ($j = 1; $j <= $excel->sheets[0]['numCols']; $j++) {
		echo "<td>";
                echo "".$excel->sheets[0]['cells'][$i][$j]."";
		echo "</td>";
        }
        echo "</tr>";
    }
    echo "<br>Done printing records<br>";

  }
  else if($ext == "csv")
  {
	$csvInOutHandle = fopen("/home/www/html/biostat/$myTimestamp/$csvFile", "r");
	$row = 1;
	if (($csvInOutHandle = fopen("/home/www/html/biostat/$myTimestamp/$csvFile", "r")) !== FALSE) {
	echo "<table>";
    	while (($data = fgetcsv($csvInOutHandle, 1000, ",")) !== FALSE) {
        	$num = count($data);
		echo "<td>";
        	$row++;
        	for ($c=0; $c < $num; $c++) {
            		//echo $data[$c] . "<br />\n";
	    		echo "<td>$data[$c]" . "</td>\n";
        	}
		echo "</tr>";
    	}
    	echo "</table>";
    	fclose($csvInOutHandle);
  	}
  }

//DISPLAY THE OUTPUT IMAGES TO THE USER
  if($submitted=='1')
  {
    echo "<br><img src=\"$myTimestamp/timeSeries.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/normalDistCurve.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/ggplotTimeSeries.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/ggplotNormDist.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/ggplotResiduals.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/standardizedResiduals.jpg\"&nbsp;&nbsp;";
    echo "<br><img src=\"$myTimestamp/ggplotStandardResiduals.jpg\"&nbsp;&nbsp;";

//IF SINGLE REGRESSION, SHOW THAT CURVE
    if($analysisCheck=="SL")
    {
      echo "<br><img src=\"$myTimestamp/singleRegression.jpg\"&nbsp;&nbsp;";
      echo "<br><img src=\"$myTimestamp/ggplotNLS.jpg\"&nbsp;&nbsp;";
    }

//IF BI LOGISTIC, SHOW THAT CURVE
    if($analysisCheck=="BL")
    {
      echo "<br><img src=\"$myTimestamp/BilogisticCurve.jpg\"<br>";
    }

//IF TRI LOGISTIC SHOW THAT CURVE
    if($analysisCheck=="TL")
    {
      echo "<br><img src=\"$myTimestamp/TrilogisticCurve.jpg\"<br>";
    }

  }

//DISPLAY A LINK SO USER CAN DOWNLOAD THE RESULTS
  echo "<br><a href=\"$myTimestamp/$myZipFile\">DOWNLOAD RESULTS</a><br>";
}
else{
  //echo "<br>nothing<br>";
}

?>
