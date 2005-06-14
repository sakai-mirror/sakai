<?xml version="1.0" ?>
<!--
* <p>Title: NavigoProject.org</p>
* <p>Description: ASI Author XML Style Sheet </p>
* <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
* <p>Company: </p>
* @author <a href="mailto:rshastri@indiana.edu">Rashmi Shastri</a>
* @version $Id: createWeekDropdown.xsl,v 1.1.1.1 2004/07/28 21:32:09 rgollub.stanford.edu Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template name="weekDropdown">
   <xsl:param name="wSelected"/>
  	<option value='1'><xsl:if test="$wSelected=1" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>1</option>
	<option value='2'><xsl:if test="$wSelected=2" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>2</option>
	<option value='3'><xsl:if test="$wSelected=3" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>3</option>
	<option value='4'><xsl:if test="$wSelected=4" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>4</option>
	<option value='5'><xsl:if test="$wSelected=5" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>5</option>
	<option value='6'><xsl:if test="$wSelected=6" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>6</option>
	<option value='7'><xsl:if test="$wSelected=7" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>7</option>
	<option value='8'><xsl:if test="$wSelected=8" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>8</option>
	<option value='9'><xsl:if test="$wSelected=9" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>9</option>
	<option value='10'><xsl:if test="$wSelected=10" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>10</option>
	<option value='11'><xsl:if test="$wSelected=11" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>11</option>
	<option value='12'><xsl:if test="$wSelected=12" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>12</option>
	<option value='13'><xsl:if test="$wSelected=13" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>13</option>
	<option value='14'><xsl:if test="$wSelected=14" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>14</option>
	<option value='15'><xsl:if test="$wSelected=15" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>15</option>
	<option value='16'><xsl:if test="$wSelected=16" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>16</option>
	<option value='17'><xsl:if test="$wSelected=17" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>17</option>
	<option value='18'><xsl:if test="$wSelected=18" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>18</option>
	<option value='19'><xsl:if test="$wSelected=19" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>19</option>
	<option value='20'><xsl:if test="$wSelected=20" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>20</option>
	<option value='21'><xsl:if test="$wSelected=21" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>21</option>
	<option value='22'><xsl:if test="$wSelected=22" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>22</option>
	<option value='23'><xsl:if test="$wSelected=23" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>23</option>
	<option value='24'><xsl:if test="$wSelected=24" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>24</option>
	<option value='25'><xsl:if test="$wSelected=25" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>25</option>
	<option value='26'><xsl:if test="$wSelected=26" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>26</option>
	<option value='27'><xsl:if test="$wSelected=27" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>27</option>
	<option value='28'><xsl:if test="$wSelected=28" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>28</option>
	<option value='29'><xsl:if test="$wSelected=29" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>29</option>
	<option value='30'><xsl:if test="$wSelected=30" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>30</option>
	<option value='31'><xsl:if test="$wSelected=31" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>31</option>
	<option value='32'><xsl:if test="$wSelected=32" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>32</option>
	<option value='33'><xsl:if test="$wSelected=33" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>33</option>
	<option value='34'><xsl:if test="$wSelected=34" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>34</option>
	<option value='35'><xsl:if test="$wSelected=35" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>35</option>
	<option value='36'><xsl:if test="$wSelected=36" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>36</option>
	<option value='37'><xsl:if test="$wSelected=37" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>37</option>
	<option value='38'><xsl:if test="$wSelected=38" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>38</option>
	<option value='39'><xsl:if test="$wSelected=39" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>39</option>
	<option value='40'><xsl:if test="$wSelected=40" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>40</option>
	<option value='41'><xsl:if test="$wSelected=41" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>41</option>
	<option value='42'><xsl:if test="$wSelected=42" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>42</option>
	<option value='43'><xsl:if test="$wSelected=43" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>43</option>
	<option value='44'><xsl:if test="$wSelected=44" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>44</option>
	<option value='45'><xsl:if test="$wSelected=45" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>45</option>
	<option value='46'><xsl:if test="$wSelected=46" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>46</option>
	<option value='47'><xsl:if test="$wSelected=47" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>47</option>
	<option value='48'><xsl:if test="$wSelected=48" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>48</option>
	<option value='49'><xsl:if test="$wSelected=49" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>49</option>
	<option value='50'><xsl:if test="$wSelected=50" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>50</option>
	<option value='51'><xsl:if test="$wSelected=51" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>51</option>
	<option value='52'><xsl:if test="$wSelected=52" ><xsl:attribute name="selected">true</xsl:attribute></xsl:if>52</option>
  </xsl:template>
</xsl:stylesheet>
				