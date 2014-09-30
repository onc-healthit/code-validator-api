<% String rootContext = request.getRequestURL().toString().replace("/About", ""); %>

<html>
<head>
	<title>Vocabulary Validation Services API</title>
	
	<script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
	
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">

	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css">

	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	
	<style>
	
		.row {
			margin-left: 30px;
			margin-right:30px;
		}
			
		.page
		{
			margin-left: 20px;
		}
		
	
	</style>
	
</head>

<body>
<div class="jumbotron">
  <h1>Vocabulary Validation Services API</h1>
  <p>A major issue in clinical document validation is the lack of validation of vocabulary value set validations.  In healthcare, there are multiple standard vocabulary value sets, such as SNOMED CT, RxNorm, LOINC, and ICD-10-CM, which are used to define clinical concepts used in clinical quality measures.  To solve this issue, an extensible system was developed to support validation against common vocabulary value sets.   
The primary use cases of this system include validating that a code exists in a specific vocabulary and validating that a display name exists in a specific vocabulary.

</p>
</div>

<div class="row">
	<div class="col-lg-9 col-md-9 col-sm-9">
		<div id="validateCode" class="page-header">
  			<h1>Validate Code</h1>
		</div>
		
		<div id="validateCodePage" class="page">
  			<p>Validate Code verifies the existence of a code in a specified vocabulary.</p>
  			
  			<h2>URL</h2>
  			<p><%= rootContext %>/validateCode/&lt;vocabulary&gt;/&lt;code&gt;  			  			
  			<h2>Data Sets</h2>
  			<p>
  				The following vocabularies are supported:
  				<ul>
  					<li>SNOMED</li>
  				</ul>
  				
  				Datasets are loaded from the following GitHub repository:  TBD
   			</p>
  			<h2>Validation Response</h2>
  			<p>
  				The validation response message returns a simple JSON object.  The simple object only includes a boolean attribute named "result".  If the validation passes, and the code exists in the specified vocabulary, "result" will be true.  If the validation fails and the code could not be found in the specified vocabulary, "result" will be false.
  			</p>
  			<p>
  				Note: Display name validation is case-insensitive.
  			</p>
  			<h2>Example Usage</h2>
  			<p>
  				The following example will validate the code value of "C-D2223", from the "SNOWMED" vocabulary value set.
  			</p>
  			<p>	
  				<a href="<%= rootContext %>/validateCode/SNOMED/C-D2223"><%= rootContext %>/validateCode/SNOMED/C-D2223</a>
  			</p>		
		</div>

		<div id="validateName" class="page-header">
  			<h1>Validate Display Name</h1>
		</div>
		
		<div id="validateNamePage" class="page">
  			<p>Validate Code verifies the existence of a code in a specified vocabulary.</p>
  			
  			<h2>URL</h2>
  			<p><%= rootContext %>/validateName/&lt;vocabulary&gt;/&lt;code&gt;  			  			
  			<h2>Data Sets</h2>
  			<p>
  				The following vocabularies are supported:
  				<ul>
  					<li>SNOMED</li>
  				</ul>
  				
  				Datasets are loaded from the following GitHub repository:  TBD
   			</p>
  			<h2>Validation Response</h2>
  			<p>
  				The validation response message returns a simple JSON object.  The simple object only includes a boolean attribute named "result".  If the validation passes, and the code exists in the specified vocabulary, "result" will be true.  If the validation fails and the code could not be found in the specified vocabulary, "result" will be false.
  			</p>
  			<p>
  				Note: Display name validation is case-insensitive.
  			</p>
  			<h2>Example Usage</h2>
  			<p>
  				The following example will validate the display name value of "CUTTER PREMISE AND STABLE SPRAY (PRODUCT)", from the "SNOWMED" vocabulary value set.
  			</p>
  			<p>
  				<a href="<%= rootContext %>/validateName/SNOMED/CUTTER%20PREMISE%20AND%20STABLE%20SPRAY%20(PRODUCT)"><%= rootContext %>/validateName/SNOMED/CUTTER%20PREMISE%20AND%20STABLE%20SPRAY%20(PRODUCT)</a>
  			</p>
		</div>
	</div>
	<div class="col-lg-3 col-md-3 col-sm-3 hidden-xs">
		<ul class="nav nav-pills nav-stacked">
			<li class="page-header"><h1>Service API</h1></li>
  			<li><a href="#validateCode">Validate Code</a></li>
  			<li><a href="#validateName">Validate Display Name</a></li>
		</ul>
		<ul class="nav nav-pills nav-stacked">
			<li class="page-header"><h1>Project Links</h1></li>
  			<li><a href="https://github.com/">Project Repository</a></li>
		</ul>
	</div>
</div>



</body>

</html>